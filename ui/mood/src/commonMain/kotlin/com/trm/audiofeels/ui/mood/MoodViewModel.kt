package com.trm.audiofeels.ui.mood

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.SharingStarted

class MoodViewModel(
  savedStateHandle: SavedStateHandle,
  private val playlistsRepository: PlaylistsRepository,
) : ViewModel() {
  val mood = Mood.valueOf(requireNotNull(savedStateHandle[MOOD_KEY]))

  val playlists: RestartableStateFlow<LoadableState<List<Playlist>>> =
    loadableStateFlowOf { playlistsRepository.getPlaylists(mood) }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Loading,
      )

  companion object {
    @Composable
    operator fun invoke(mood: Mood, playlistsRepository: PlaylistsRepository): MoodViewModel =
      viewModel {
        MoodViewModel(
          savedStateHandle = createSavedStateHandle().apply { set(MOOD_KEY, mood.name) },
          playlistsRepository = playlistsRepository,
        )
      }

    private const val MOOD_KEY = "Mood"
  }
}
