package com.trm.audiofeels.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlin.reflect.KClass
import kotlin.reflect.cast
import me.tatarka.inject.annotations.Inject

@Inject
class DiscoverViewModelFactory(private val playlistsRepository: PlaylistsRepository) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
    modelClass.cast(DiscoverViewModel(playlistsRepository))
}
