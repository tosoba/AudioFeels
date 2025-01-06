package com.trm.audiofeels.core.player

import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.HttpDataSource.HttpDataSourceException
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.AppCoroutineScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.base.util.lazyAsync
import com.trm.audiofeels.core.player.mapper.toMediaItem
import com.trm.audiofeels.core.player.mapper.toState
import com.trm.audiofeels.domain.model.PlayerConstants
import com.trm.audiofeels.domain.model.PlayerError
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import io.github.aakira.napier.Napier
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
  private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
  override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

  override val currentPositionMs: StateFlow<Long> =
    flow {
        while (currentCoroutineContext().isActive) {
          emit(mediaBrowser.await().currentPosition)
          delay(100L)
        }
      }
      .stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = PlayerConstants.DEFAULT_START_POSITION_MS,
      )

  @OptIn(UnstableApi::class)
  private val mediaBrowser: Deferred<MediaBrowser> by
    scope.lazyAsync {
      MediaBrowser.Builder(
          context,
          SessionToken(context, ComponentName(context, PlayerService::class.java)),
        )
        .buildAsync()
        .await()
        .apply {
          addListener(
            object : Player.Listener {
              override fun onEvents(player: Player, events: Player.Events) {
                if (
                  events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_MEDIA_METADATA_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED,
                  )
                ) {
                  _playerState.value = player.toState()
                }
              }

              override fun onPlayerError(error: PlaybackException) {
                Napier.e(
                  message = "Error code: ${error.errorCode}\nMessage:${error.localizedMessage}",
                  throwable = error,
                  tag = this@AudioPlayerConnection.javaClass.simpleName,
                )

                updatePlayerState(error)
              }

              private fun updatePlayerState(exception: PlaybackException) {
                _playerState.update {
                  PlayerState.Error(
                    error =
                      when (exception.cause) {
                        is HttpDataSource.InvalidResponseCodeException -> {
                          PlayerError.INVALID_HOST_ERROR
                        }
                        is HttpDataSourceException -> {
                          PlayerError.CONNECTION_ERROR
                        }
                        else -> {
                          PlayerError.OTHER_ERROR
                        }
                      },
                    previousState = it,
                  )
                }
              }
            }
          )
        }
    }

  override fun toggleIsPlaying() {
    val playerState = _playerState.value as? PlayerState.Initialized ?: return
    withMediaBrowser { if (playerState.isPlaying) pause() else play() }
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

  override fun skipTo(itemIndex: Int, positionMs: Long) {
    withMediaBrowser {
      seekTo(itemIndex, positionMs)
      play()
    }
  }

  override fun play(
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
      repeatMode = Player.REPEAT_MODE_OFF
    }
  }

  private fun Iterable<Track>.toMediaItems(host: String): List<MediaItem> = map { track ->
    track.toMediaItem(host)
  }

  override fun reset() {
    withMediaBrowser(MediaBrowser::clearMediaItems)
    _playerState.value = PlayerState.Idle
  }

  private fun withMediaBrowser(action: MediaBrowser.() -> Unit) {
    scope.launch { mediaBrowser.await().run(action) }
  }
}
