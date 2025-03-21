package com.trm.audiofeels.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DiscoverViewModel(private val playlistsRepository: PlaylistsRepository) : ViewModel() {
  val carryOnPlaylists: StateFlow<LoadableState<List<CarryOnPlaylist>>> =
    playlistsRepository
      .getCarryOnPlaylistsFlow()
      .map { LoadableState.Idle(it) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = LoadableState.Loading,
      )

  val favouritePlaylists: StateFlow<LoadableState<List<Playlist>>> =
    playlistsRepository
      .getFavouritePlaylistsFlow()
      .map { LoadableState.Idle(it) }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = LoadableState.Loading,
      )

  val trendingPlaylists: RestartableStateFlow<LoadableState<List<Playlist>>> =
    loadableStateFlowOf { playlistsRepository.getPlaylists(null) }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Loading,
      )
}
