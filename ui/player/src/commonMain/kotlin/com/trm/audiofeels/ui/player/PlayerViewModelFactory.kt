package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.trm.audiofeels.core.player.PlayerConnection
import kotlin.reflect.KClass
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerViewModelFactory(private val playerConnection: PlayerConnection) :
  ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
    PlayerViewModel(playerConnection) as T
}
