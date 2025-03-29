package com.trm.audiofeels.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.SharingStarted

class MoodViewModel(internal val mood: Mood, private val playlistsRepository: PlaylistsRepository) :
  ViewModel() {
  val playlists: RestartableStateFlow<LoadableState<List<Playlist>>> =
    loadableStateFlowOf { playlistsRepository.getPlaylists(mood = mood.name) }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Loading,
      )
}
