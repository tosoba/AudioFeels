package com.trm.audiofeels.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.SharingStarted

class DiscoverViewModel(private val playlistsRepository: PlaylistsRepository) : ViewModel() {
  val playlists: RestartableStateFlow<LoadableState<List<Playlist>>> =
    loadableStateFlowOf { playlistsRepository.getPlaylists(null) }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Loading,
      )
}
