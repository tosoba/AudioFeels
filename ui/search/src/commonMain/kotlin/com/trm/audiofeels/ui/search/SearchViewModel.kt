package com.trm.audiofeels.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.SharingStarted

class SearchViewModel(private val playlistsRepository: PlaylistsRepository) : ViewModel() {
  val playlists =
    loadableStateFlowOf { playlistsRepository.searchPlaylists("rock") }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Loading,
      )
}
