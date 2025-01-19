package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.PlatformContext
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.core.base.util.roundTo
import com.trm.audiofeels.core.ui.compose.util.loadImageBitmapOrNull
import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.PlaybackRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import io.github.aakira.napier.Napier
import kotlin.math.roundToLong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playerConnection: PlayerConnection,
  private val getPlayerInputUseCase: GetPlayerInputUseCase,
  private val playbackRepository: PlaybackRepository,
  private val imageLoader: ImageLoader,
  private val platformContext: PlatformContext,
) : ViewModel() {
  val viewState: RestartableStateFlow<PlayerViewState> =
    playbackRepository
      .getPlaybackPlaylistFlow()
      .distinctUntilChangedBy { it?.id }
      .flatMapLatest { playlist ->
        playlist?.id?.let { playlistId ->
          loadableStateFlowOf { getPlayerInputUseCase(playlistId) }
            .onEach { input ->
              if (input is LoadableState.Success && input.value.start.autoPlay) {
                enqueue(input.value)
              }
            }
            .flatMapLatest { playerInput ->
              when (playerInput) {
                is LoadableState.Success -> {
                  var lastArtworkUrl: String? = null

                  playerConnection.playerState
                    .transformLatest { playerState ->
                      val trackArtworkUrl = getTrackArtworkUrl(playerState, playerInput.value)
                      if (trackArtworkUrl != lastArtworkUrl) {
                        lastArtworkUrl = trackArtworkUrl
                        emit(playerState to null)
                      }
                      emit(
                        playerState to
                          trackArtworkUrl?.let { artworkUrl ->
                            imageLoader.loadImageBitmapOrNull(artworkUrl, platformContext)
                          }
                      )
                    }
                    .combine(playerConnection.currentTrackPositionMs.distinctUntilChanged()) {
                      (playerState, trackImageBitmap),
                      currentTrackPositionMs ->
                      PlayerViewState(
                        isVisible = true,
                        playlist = playlist,
                        playerState = playerState,
                        currentTrackProgress =
                          when (playerState) {
                            is PlayerState.Enqueued -> {
                              currentTrackPositionMs.toDouble() /
                                playerState.currentTrack.duration.toDouble() /
                                1000.0
                            }
                            PlayerState.Idle,
                            is PlayerState.Error -> {
                              0.0
                            }
                          }.roundTo(3),
                        playerInput = playerInput,
                        trackImageBitmap = trackImageBitmap,
                      )
                    }
                }
                LoadableState.Loading,
                is LoadableState.Error -> {
                  flowOf(
                    PlayerViewState(
                      isVisible = true,
                      playlist = playlist,
                      playerState = PlayerState.Idle,
                      currentTrackProgress = 0.0,
                      playerInput = playerInput,
                      trackImageBitmap = null,
                    )
                  )
                }
              }
            }
            .onEach {
              Napier.d(tag = "PLAYER_STATE", message = it.playerState.toString())
              when (it.playerState) {
                PlayerState.Idle -> {
                  return@onEach
                }
                is PlayerState.Enqueued -> {
                  playbackRepository.updatePlaybackTrack(
                    trackIndex = it.playerState.currentTrackIndex,
                    trackPositionMs =
                      (it.currentTrackProgress *
                          it.playerState.currentTrack.duration.toDouble() *
                          1000.0)
                        .roundToLong(),
                  )
                }
                is PlayerState.Error -> {
                  // TODO: error handling
                }
              }
            }
        } ?: flowOf(initialPlayerViewState()).onEach { playerConnection.reset() }
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = initialPlayerViewState(),
      )

  private fun getTrackArtworkUrl(playerState: PlayerState, input: PlayerInput): String? =
    when (playerState) {
      PlayerState.Idle -> {
        input.artworkUrl
      }
      is PlayerState.Enqueued -> {
        playerState.currentTrack.artworkUrl
      }
      is PlayerState.Error -> {
        (playerState.previousState as? PlayerState.Enqueued)?.currentTrack?.artworkUrl
          ?: input.artworkUrl
      }
    }

  fun onPlaylistClick(playlist: Playlist) {
    if (viewState.value.playlist != playlist) {
      viewModelScope.launch { playbackRepository.updatePlaybackPlaylist(playlist) }
    } else {
      onPlayClick()
    }
  }

  private fun initialPlayerViewState(): PlayerViewState =
    PlayerViewState(
      isVisible = false,
      playlist = null,
      playerState = PlayerState.Idle,
      currentTrackProgress = 0.0,
      playerInput = LoadableState.Loading,
      trackImageBitmap = null,
    )

  fun onCancelPlaybackClick() {
    viewModelScope.launch { playbackRepository.clear() }
  }

  fun onPlayClick() {
    val (_, _, playerState, _, playerInput) = viewState.value
    when (playerState) {
      PlayerState.Idle -> {
        if (playerInput is LoadableState.Success) {
          enqueue(playerInput.value)
        }
      }
      is PlayerState.Enqueued -> {
        playerConnection.play()
      }
      is PlayerState.Error -> {
        return
      }
    }
  }

  private fun enqueue(input: PlayerInput) {
    val (tracks, host, start) = input
    playerConnection.enqueue(
      tracks = tracks,
      host = "https://$host",
      startTrackIndex = start.trackIndex,
      startPositionMs = start.trackPositionMs,
    )
  }

  fun onPauseClick() {
    playerConnection.pause()
  }

  fun onPreviousClick() {
    playerConnection.playPrevious()
  }

  fun onNextClick() {
    playerConnection.playNext()
  }
}
