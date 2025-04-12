package com.trm.audiofeels.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.model.map
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.repository.SuggestionsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchViewModel(
  private val playlistsRepository: PlaylistsRepository,
  private val suggestionsRepository: SuggestionsRepository,
) : ViewModel() {
  private val _query = MutableStateFlow(EMPTY_QUERY)
  val query: StateFlow<String> = _query.asStateFlow()

  private val processedQuery: Flow<String>
    get() = _query.map(String::trim).debounce(500L).distinctUntilChanged()

  private val shuffle = MutableSharedFlow<Boolean>()

  val playlists: RestartableStateFlow<LoadableState<List<Playlist>>> =
    processedQuery
      .flatMapLatest {
        if (it.length < 3) {
          flowOf(LoadableState.Loading)
        } else {
          loadableStateFlowOf {
            SearchResult(query = it, playlists = playlistsRepository.searchPlaylists(it))
          }
        }
      }
      .onEach {
        it.valueOrNull?.let { (query, playlists) ->
          if (playlists.isNotEmpty()) suggestionsRepository.saveSuggestion(query)
        }
      }
      .map { it.map { (_, playlists) -> playlists } }
      .combine(shuffle.onStart { emit(false) }) { playlists, shuffle ->
        if (shuffle) playlists.map(List<Playlist>::shuffled) else playlists
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = LoadableState.Loading,
      )

  val suggestions: StateFlow<List<String>> =
    processedQuery
      .flatMapLatest { query ->
        suggestionsRepository.getSuggestionsFlow(limit = 10).map { suggestions ->
          suggestions.filter { it != query }
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList(),
      )

  fun onQueryChange(query: String) {
    viewModelScope.launch { _query.emit(query) }
  }

  fun onShuffleClick() {
    viewModelScope.launch { shuffle.emit(true) }
  }

  private data class SearchResult(val query: String, val playlists: List<Playlist>)

  companion object {
    internal const val EMPTY_QUERY = ""
  }
}
