package com.trm.audiofeels.ui.mood

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.model.map
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MoodViewModel(
  savedStateHandle: SavedStateHandle,
  private val playlistsRepository: PlaylistsRepository,
) : ViewModel() {
  val mood = Mood.valueOf(requireNotNull(savedStateHandle[MOOD_KEY]))

  private val shuffle = MutableSharedFlow<Boolean>()

  val playlists: RestartableStateFlow<LoadableState<List<Playlist>>> =
    loadableStateFlowOf { playlistsRepository.getPlaylists(mood) }
      .combine(shuffle.onStart { emit(false) }) { playlists, shuffle ->
        if (shuffle) playlists.map(List<Playlist>::shuffled) else playlists
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Loading,
      )

  fun onShuffleClick() {
    viewModelScope.launch { shuffle.emit(true) }
  }

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
