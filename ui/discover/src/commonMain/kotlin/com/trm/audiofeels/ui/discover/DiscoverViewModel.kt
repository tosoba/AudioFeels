package com.trm.audiofeels.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class DiscoverViewModel(private val playlistsRepository: PlaylistsRepository) : ViewModel() {
  private val loadPlaylists = MutableSharedFlow<Unit>()

  val playlists: StateFlow<LoadableState<List<Playlist>>> =
    loadPlaylists
      .onStart { emit(Unit) }
      .debounce(500)
      .flatMapLatest { loadableStateFlowOf { playlistsRepository.getPlaylists(null) } }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Loading,
      )

  fun loadPlaylists() {
    viewModelScope.launch { loadPlaylists.emit(Unit) }
  }
}
