package com.trm.audiofeels.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.repository.SuggestionsRepository
import kotlin.reflect.KClass
import kotlin.reflect.cast
import me.tatarka.inject.annotations.Inject

@Inject
class SearchViewModelFactory(
  private val playlistsRepository: PlaylistsRepository,
  private val suggestionsRepository: SuggestionsRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
    modelClass.cast(
      SearchViewModel(
        playlistsRepository = playlistsRepository,
        suggestionsRepository = suggestionsRepository,
      )
    )
}
