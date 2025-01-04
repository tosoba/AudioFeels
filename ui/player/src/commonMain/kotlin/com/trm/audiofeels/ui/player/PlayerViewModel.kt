package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.model.map
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  savedPlaylist: Playlist?,
  private val savePlaylist: (Playlist) -> Unit,
  val playerConnection: PlayerConnection,
  private val repository: PlaylistsRepository,
  private val hostRetriever: HostRetriever,
) : ViewModel() {
  private val currentPlaylist = MutableSharedFlow<Playlist>(replay = 1)

  val tracks: StateFlow<LoadableState<List<Track>>> =
    currentPlaylist
      .flatMapLatest {
        loadableStateFlowOf {
          coroutineScope {
            val tracks = async { repository.getPlaylistTracks(it.id) }
            val host = async { hostRetriever.retrieveHost() }
            tracks.await() to host.await()
          }
        }
      }
      .onEach {
        if (it is LoadableState.Success) {
          val (tracks, host) = it.value
          playerConnection.play(tracks, "https://$host")
        }
      }
      .mapLatest { it.map { (tracks) -> tracks } }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LoadableState.Loading,
      )

  init {
    savedPlaylist?.let { viewModelScope.launch { currentPlaylist.emit(it) } }
  }

  fun onPlaylistClick(playlist: Playlist) {
    savePlaylist(playlist)
    viewModelScope.launch { currentPlaylist.emit(playlist) }
  }
}
