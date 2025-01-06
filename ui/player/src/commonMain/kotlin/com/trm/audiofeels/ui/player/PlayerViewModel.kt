package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.ArgumentHandle
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.model.map
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playlistHandle: ArgumentHandle<Playlist>,
  private val playerConnection: PlayerConnection,
  private val playlistsRepository: PlaylistsRepository,
  private val hostsRepository: HostsRepository,
) : ViewModel() {
  val viewState: RestartableStateFlow<PlayerViewState> =
    playlistHandle.flow
      .flatMapLatest { playlist ->
        playlist?.let {
          loadableStateFlowOf {
              coroutineScope {
                val tracks = async { playlistsRepository.getPlaylistTracks(it.id) }
                val host = async { hostsRepository.retrieveHost() }
                tracks.await() to host.await()
              }
            }
            .onEach {
              if (it is LoadableState.Success) {
                val (tracks, host) = it.value
                playerConnection.play(tracks, "https://$host")
              }
            }
            .mapLatest { it.map { (tracks) -> tracks } }
            .transformLatest { tracks ->
              if (tracks is LoadableState.Success<*>) {
                emitAll(
                  playerConnection.playerState.mapLatest {
                    PlayerViewState(isVisible = true, playerState = it, tracksState = tracks)
                  }
                )
              } else {
                emit(
                  PlayerViewState(
                    isVisible = true,
                    playerState = PlayerState.Idle,
                    tracksState = tracks,
                  )
                )
              }
            }
            .onEach {
              if (it.playerState is PlayerState.Error) {
                // TODO: error handling
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
    playlistHandle.value = playlist
  }

  private fun initialPlayerViewState(): PlayerViewState =
    PlayerViewState(
      isVisible = false,
      playerState = PlayerState.Idle,
      tracksState = LoadableState.Loading,
    )

  fun onCancelPlaybackClick() {
    playlistHandle.value = null
  }
}
