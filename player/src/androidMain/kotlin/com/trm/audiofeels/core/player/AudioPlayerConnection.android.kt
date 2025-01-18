package com.trm.audiofeels.core.player

import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.AppCoroutineScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.base.util.lazyAsync
import com.trm.audiofeels.core.player.mapper.toMediaItem
import com.trm.audiofeels.core.player.mapper.toPlayerError
import com.trm.audiofeels.core.player.mapper.toState
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
actual class AudioPlayerConnection(
  private val context: PlatformContext,
  private val scope: AppCoroutineScope,
) : PlayerConnection {
  override val currentTrackPositionMs: Flow<Long> = flow {
    while (currentCoroutineContext().isActive) {
      emit(mediaBrowser.await().currentPosition)
      delay(1_000L)
    }
  }

  override val playerState: Flow<PlayerState> =
    callbackFlow {
        val browser = mediaBrowser.await()
        trySend(browser.toState())

        val listener =
          object : Player.Listener {
            private var previousState: PlayerState = PlayerState.Idle

            override fun onEvents(player: Player, events: Player.Events) {
              if (
                events.containsAny(
                  Player.EVENT_PLAYBACK_STATE_CHANGED,
                  Player.EVENT_MEDIA_METADATA_CHANGED,
                  Player.EVENT_PLAY_WHEN_READY_CHANGED,
                )
              ) {
                val state = player.toState()
                Napier.d(
                  message = state.toString(),
                  tag = this@AudioPlayerConnection.javaClass.simpleName,
                )
                trySend(state.also { previousState = it })
              }
            }

            override fun onPlayerError(error: PlaybackException) {
              Napier.e(
                message = "Error code: ${error.errorCode}\nMessage:${error.localizedMessage}",
                throwable = error,
                tag = this@AudioPlayerConnection.javaClass.simpleName,
              )

              trySend(
                PlayerState.Error(error = error.toPlayerError(), previousState = previousState)
                  .also { previousState = it }
              )
            }
          }
        browser.addListener(listener)

        awaitClose { browser.removeListener(listener) }
      }
      .conflate()

  @OptIn(UnstableApi::class)
  private val mediaBrowser: Deferred<MediaBrowser> by
    scope.lazyAsync {
      MediaBrowser.Builder(
          context,
          SessionToken(context, ComponentName(context, PlayerService::class.java)),
        )
        .buildAsync()
        .await()
    }

  override fun play() {
    withMediaBrowser(MediaBrowser::play)
  }

  override fun pause() {
    withMediaBrowser(MediaBrowser::pause)
  }

  override fun playPrevious() {
    withMediaBrowser {
      seekToPrevious()
      play()
    }
  }

  override fun playNext() {
    withMediaBrowser {
      seekToNext()
      play()
    }
  }

  override fun skipTo(positionMs: Long) {
    withMediaBrowser {
      seekTo(positionMs)
      play()
    }
  }

  override fun skipTo(trackIndex: Int, positionMs: Long) {
    withMediaBrowser {
      seekTo(trackIndex, positionMs)
      play()
    }
  }

  override fun enqueue(
    tracks: List<Track>,
    host: String,
    autoPlay: Boolean,
    startTrackIndex: Int,
    startPositionMs: Long,
  ) {
    withMediaBrowser {
      setMediaItems(tracks.toMediaItems(host), startTrackIndex, startPositionMs)
      prepare()
      if (autoPlay) play()
      repeatMode = Player.REPEAT_MODE_ALL
    }
  }

  private fun Iterable<Track>.toMediaItems(host: String): List<MediaItem> = map { track ->
    track.toMediaItem(host)
  }

  override fun reset() {
    withMediaBrowser(MediaBrowser::clearMediaItems)
  }

  private fun withMediaBrowser(action: MediaBrowser.() -> Unit) {
    scope.launch { mediaBrowser.await().run(action) }
  }
}
