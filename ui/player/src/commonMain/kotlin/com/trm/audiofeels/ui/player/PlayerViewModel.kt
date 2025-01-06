package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.ArgumentHandle
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.model.map
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playlistHandle: ArgumentHandle<Playlist>,
  private val playerConnection: PlayerConnection,
  private val playlistsRepository: PlaylistsRepository,
  private val hostsRepository: HostsRepository,
) : ViewModel() {
  val playerVisible: StateFlow<Boolean> =
    playlistHandle.flow
      .map { it != null }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = false,
      )

  val tracks: StateFlow<LoadableState<List<Track>>> =
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
        }
          ?: flowOf<LoadableState.Success<List<Track>>>(LoadableState.Success(emptyList())).onEach {
            playerConnection.reset()
          }
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly, // TODO: change to lazily
        initialValue = LoadableState.Loading,
      )

  fun onPlaylistClick(playlist: Playlist) {
    playlistHandle.value = playlist
  }

  fun onCancelPlaybackClick() {
    playlistHandle.value = null
  }
}
