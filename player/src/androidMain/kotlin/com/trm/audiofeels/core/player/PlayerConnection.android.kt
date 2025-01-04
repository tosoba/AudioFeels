package com.trm.audiofeels.core.player

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import co.touchlab.kermit.Logger
import com.trm.audiofeels.core.base.util.AppCoroutineScope
import com.trm.audiofeels.core.base.util.lazyAsync
import com.trm.audiofeels.core.base.util.onCompletion
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.network.monitor.NetworkMonitor
import com.trm.audiofeels.core.player.model.PlayerConstants
import com.trm.audiofeels.core.player.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking

@OptIn(UnstableApi::class)
actual class PlayerPlatformConnection(
  private val context: Context,
  private val hostRetriever: HostRetriever,
  scope: AppCoroutineScope,
  networkMonitor: NetworkMonitor,
) : PlayerConnection {
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
                // can happen for invalid host - recover by reinitializing playback (using
                // previously saved parameters) and host fetcher instead of host retriever?

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

  private val _playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Idle)
  override val playerStateFlow: StateFlow<PlayerState> = _playerStateFlow.asStateFlow()

  override val currentPositionMsFlow: StateFlow<Long> =
    flow {
        while (currentCoroutineContext().isActive) {
          val currentPosition = mediaBrowser.await().currentPosition
          emit(currentPosition)
          delay(100L)
        }
      }
      .stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = PlayerConstants.DEFAULT_START_POSITION_MS,
      )

  override fun toggleIsPlaying() {
    val playerState = _playerStateFlow.value as? PlayerState.Initialized ?: return
    mediaBrowser.onCompletion { if (playerState.isPlaying) pause() else play() }
  }

  override fun playPrevious() {
    mediaBrowser.onCompletion {
      seekToPrevious()
      play()
    }
  }

  override fun playNext() {
    mediaBrowser.onCompletion {
      seekToNext()
      play()
    }
  }

  override fun skipTo(positionMs: Long) {
    mediaBrowser.onCompletion {
      seekTo(positionMs)
      play()
    }
  }

  override fun skipTo(itemIndex: Int, positionMs: Long) {
    mediaBrowser.onCompletion {
      seekTo(itemIndex, positionMs)
      play()
    }
  }

  override fun play(
    tracks: List<Track>,
    autoPlay: Boolean,
    startIndex: Int,
    startPositionMs: Long,
  ) {
    mediaBrowser.onCompletion {
      setMediaItems(tracks.toMediaItems(), startIndex, startPositionMs)
      prepare()
      if (autoPlay) play()
      repeatMode = Player.REPEAT_MODE_OFF
    }
  }

  private fun Iterable<Track>.toMediaItems(): List<MediaItem> {
    // TODO: retrieveHost could fail on getHosts network call - add error handling
    // (no error handling may cause a crash)
    // Consider making play function suspending since there might be a need to fetch a host here...
    // Rethink edge cases carefully (even if host is fetched by a previous call a user could clear
    // app data before pressing play - rare case).
    val host = "https://${runBlocking { hostRetriever.retrieveHost() }}"
    return map { track -> track.toMediaItem(host) }
  }

  override fun reset() {
    mediaBrowser.onCompletion(MediaBrowser::clearMediaItems)
    _playerStateFlow.value = PlayerState.Idle
  }

  private fun updateMusicState(player: Player) {
    _playerStateFlow.update {
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
