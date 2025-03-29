package com.trm.audiofeels.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchViewModel(private val playlistsRepository: PlaylistsRepository) : ViewModel() {
  private val queryFlow = MutableSharedFlow<String>()

  val result: RestartableStateFlow<LoadableState<SearchResult>> =
    queryFlow
      .map(String::trim)
      .debounce(500L)
      .distinctUntilChanged()
      .flatMapLatest {
        if (it.length < 3) flowOf(LoadableState.Idle(SearchResult(it, emptyList())))
        else loadableStateFlowOf { SearchResult(it, playlistsRepository.searchPlaylists(it)) }
      }
      .onEach {
        it.valueOrNull?.let { (query, playlists) ->
          if (playlists.isNotEmpty()) {
            // TODO: save query as suggestion
          }
        }
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Idle(SearchResult("", emptyList())),
      )

  fun onQueryChange(query: String) {
    viewModelScope.launch { queryFlow.emit(query) }
  }
}
