package com.trm.audiofeels.core.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED
import androidx.media3.common.Player.EVENT_PLAYBACK_STATE_CHANGED
import androidx.media3.common.Player.EVENT_PLAY_WHEN_READY_CHANGED
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.trm.audiofeels.core.base.util.ApplicationCoroutineScope
import com.trm.audiofeels.core.base.util.onCompletion
import com.trm.audiofeels.core.network.HostRetriever
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
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

actual class PlayerPlatformConnection(
  private val context: Context,
  private val hostRetriever: HostRetriever,
  scope: ApplicationCoroutineScope,
) : PlayerConnection {
  private val mediaBrowser = CompletableDeferred<MediaBrowser>()

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
                  // TODO: connect network monitor on network exceptions
                }

                override fun onEvents(player: Player, events: Player.Events) {
                  if (
                    events.containsAny(
                      EVENT_PLAYBACK_STATE_CHANGED,
                      EVENT_MEDIA_METADATA_CHANGED,
                      EVENT_PLAY_WHEN_READY_CHANGED,
                    )
                  ) {
                    // TODO: updateMusicState(player)
                  }
                }
              }
            )
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
      .stateIn(scope, SharingStarted.Lazily, PlayerConstants.DEFAULT_START_POSITION_MS)

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
      // TODO: setMediaItems(tracks.asMediaItems(), startIndex, startPositionMs)
      prepare()
      if (autoPlay) play()
      repeatMode = REPEAT_MODE_ALL
    }
  }

  override fun reset() {
    mediaBrowser.onCompletion(MediaBrowser::clearMediaItems)
    _playerStateFlow.value = PlayerState.Idle
  }
}
