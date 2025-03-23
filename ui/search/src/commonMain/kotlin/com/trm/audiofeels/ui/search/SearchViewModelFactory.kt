package com.trm.audiofeels.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlin.reflect.KClass
import me.tatarka.inject.annotations.Inject

@Inject
class SearchViewModelFactory(private val playlistsRepository: PlaylistsRepository) :
  ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
    SearchViewModel(playlistsRepository) as T
}
