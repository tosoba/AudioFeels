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
import com.trm.audiofeels.core.base.util.ApplicationCoroutineScope
import com.trm.audiofeels.core.base.util.onCompletion
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.player.model.PlayerConstants
import com.trm.audiofeels.core.player.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.CompletableDeferred
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(UnstableApi::class)
actual class PlayerPlatformConnection(
  private val context: Context,
  private val hostRetriever: HostRetriever,
  scope: ApplicationCoroutineScope,
) : PlayerConnection {
  private val mediaBrowser = CompletableDeferred<MediaBrowser>()

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

  init {
    scope.launch {
      mediaBrowser.complete(
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
                  Logger.e(messageString = "ERROR", throwable = error, tag = javaClass.simpleName)
                  // TODO: handle androidx.media3.exoplayer.ExoPlaybackException: Source error on
                  // track with id not found - possibly skip to next item
                  // TODO: connect network monitor on network exceptions
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
      )
    }
  }

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
    // TODO: this technically has a chance of failing due to redirect response on pingHost - add
    // error handling (no error handling causes a crash)

    // io.ktor.client.plugins.RedirectResponseException: Unhandled redirect: GET
    // https://blockchange-audius-discovery-04.bdnodes.net/v1. Status: 308 PERMANENT REDIRECT. Text:
    // "<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
    //
    //      <title>Redirecting...</title>
    //
    //      <h1>Redirecting...</h1>
    //
    //      <p>You should be redirected automatically to target URL: <a
    // href="http://blockchange-audius-discovery-04.bdnodes.net/v1/">http://blockchange-audius-discovery-04.bdnodes.net/v1/</a>.  If not click the link."

    // TODO: It could also fail on getHosts call - add error handling (no error handling causes a
    // crash)

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
