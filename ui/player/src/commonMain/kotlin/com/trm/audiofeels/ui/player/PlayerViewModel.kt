package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.ImageRequest
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.core.ui.compose.util.toComposeImageBitmap
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaybackRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import io.github.aakira.napier.Napier
import kotlin.coroutines.resume
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playerConnection: PlayerConnection,
  private val playlistsRepository: PlaylistsRepository,
  private val hostsRepository: HostsRepository,
  private val playbackRepository: PlaybackRepository,
  private val imageLoader: ImageLoader,
  private val platformContext: PlatformContext,
) : ViewModel() {
  val viewState: RestartableStateFlow<PlayerViewState> =
    playbackRepository
      .getPlaybackPlaylistFlow()
      .flatMapLatest { playlist ->
        playlist?.id?.let { playlistId ->
          loadableStateFlowOf {
              coroutineScope { // TODO: usecase for that
                val tracks = async { playlistsRepository.getPlaylistTracks(playlistId) }
                val host = async { hostsRepository.retrieveHost() }
                val start = async { playbackRepository.getPlaybackStart() }
                PlayerInput(tracks = tracks.await(), host = host.await(), start = start.await())
              }
            }
            .onEach { input ->
              if (input !is LoadableState.Success) return@onEach

              val (tracks, host, start) = input.value
              if (start.autoPlay) {
                playerConnection.play(
                  tracks = tracks,
                  host = "https://$host",
                  startTrackIndex = start.trackIndex,
                )
              }
            }
            .transformLatest { input ->
              when (input) {
                is LoadableState.Success -> {
                  emitAll(
                    playerConnection.playerState.mapLatest { playerState ->
                      PlayerViewState(
                        isVisible = true,
                        playerState = playerState,
                        playerInput = input,
                        trackImageBitmap =
                          when (playerState) {
                            is PlayerState.Enqueued -> {
                              playerState.currentTrack.artworkUrl?.let { artworkUrl ->
                                // TODO: extension for that (ImageExtensions - CoilExtensions)
                                suspendCancellableCoroutine { continuation ->
                                  imageLoader.enqueue(
                                    ImageRequest.Builder(platformContext)
                                      .data(artworkUrl)
                                      .target(
                                        onSuccess = {
                                          continuation.resume(it.toComposeImageBitmap())
                                        },
                                        onError = { continuation.resume(null) },
                                      )
                                      .build()
                                  )
                                }
                              }
                            }
                            PlayerState.Idle,
                            is PlayerState.Error -> {
                              null
                            }
                          },
                      )
                    }
                  )
                }
                LoadableState.Loading,
                is LoadableState.Error -> {
                  emit(
                    PlayerViewState(
                      isVisible = true,
                      playerState = PlayerState.Idle,
                      playerInput = input,
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
                  playbackRepository.updatePlaybackTrackIndex(it.playerState.currentTrackIndex)
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

  fun onPlaylistClick(playlist: Playlist) {
    viewModelScope.launch { playbackRepository.updatePlaybackPlaylist(playlist) }
  }

  private fun initialPlayerViewState(): PlayerViewState =
    PlayerViewState(
      isVisible = false,
      playerState = PlayerState.Idle,
      playerInput = LoadableState.Loading,
      trackImageBitmap = null,
    )

  fun onCancelPlaybackClick() {
    viewModelScope.launch { playbackRepository.clear() }
  }

  fun onPlayClick() {
    val (_, playerState, playerInput) = viewState.value
    when (playerState) {
      PlayerState.Idle -> {
        if (playerInput !is LoadableState.Success) return
        val (tracks, host, start) = playerInput.value
        playerConnection.play(
          tracks = tracks,
          host = "https://$host",
          startTrackIndex = start.trackIndex,
        )
      }
      is PlayerState.Enqueued -> {
        playerConnection.play()
      }
      is PlayerState.Error -> return
    }
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
