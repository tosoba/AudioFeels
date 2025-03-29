package com.trm.audiofeels.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlin.reflect.KClass
import kotlin.reflect.cast
import me.tatarka.inject.annotations.Inject

@Inject
class MoodViewModelFactory(private val playlistsRepository: PlaylistsRepository) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
    modelClass.cast(
      MoodViewModel(
        mood = requireNotNull(extras[MoodKey]),
        playlistsRepository = playlistsRepository,
      )
    )
}
