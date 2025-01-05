package com.trm.audiofeels.core.player

import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import co.touchlab.kermit.Logger
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.AppCoroutineScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.base.util.lazyAsync
import com.trm.audiofeels.core.network.monitor.NetworkMonitor
import com.trm.audiofeels.core.player.model.PlayerConstants
import com.trm.audiofeels.core.player.model.PlayerState
import com.trm.audiofeels.domain.model.Track
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
actual class PlayerPlatformConnection(
  private val context: PlatformContext,
  private val scope: AppCoroutineScope,
  networkMonitor: NetworkMonitor, // TODO: consider moving this to PlayerVM
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
              override fun onPlayerError(error: PlaybackException) {
                Logger.e(
                  messageString =
                    "Error code: ${error.errorCode}\nMessage:${error.localizedMessage}",
                  throwable = error,
                  tag = PlaybackException::class.java.simpleName,
                )

                // TODO: handle androidx.media3.exoplayer.ExoPlaybackException: Source error
                // Caused by:
                // androidx.media3.datasource.HttpDataSource$InvalidResponseCodeException:
                // Response code: 525
                // can happen for invalid host - recover (automatically like for network errors?)
                // by reinitializing playback (using previously saved parameters)
                // and host fetcher instead of host retriever?

                // when (val cause = error.cause) {
                //                  is InvalidResponseCodeException -> {
                //                    scope.launch {}
                //                  }
                //                }

                // TODO: connect network monitor on network exceptions (see exactly which
                // exception occurs on no internet connection)
              }

              override fun onEvents(player: Player, events: Player.Events) {
                if (
                  events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_MEDIA_METADATA_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED,
                  )
                ) {
                  updateMusicState(player)
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
    startIndex: Int,
    startPositionMs: Long,
  ) {
    withMediaBrowser {
      setMediaItems(tracks.toMediaItems(host), startIndex, startPositionMs)
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

  private fun updateMusicState(player: Player) {
    _playerState.update {
      with(player) {
        currentMediaItem?.let { item ->
          PlayerState.Initialized(
            currentTrack = item.toTrack(),
            currentTrackIndex = currentMediaItemIndex,
            tracksCount = mediaItemCount,
            playbackState = enumPlaybackStateOf(playbackState),
            isPlaying = isPlaying,
            trackDurationMs =
              duration.takeIf { it != C.TIME_UNSET } ?: PlayerConstants.DEFAULT_DURATION_MS,
          )
        } ?: PlayerState.Idle
      }
    }
  }
}
