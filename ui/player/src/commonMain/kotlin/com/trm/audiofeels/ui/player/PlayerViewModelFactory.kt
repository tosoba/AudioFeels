package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.repository.VisualizationRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import kotlin.reflect.KClass
import kotlin.reflect.cast
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerViewModelFactory(
  private val playerConnection: PlayerConnection,
  private val getPlayerInputUseCase: GetPlayerInputUseCase,
  private val playlistsRepository: PlaylistsRepository,
  private val visualizationRepository: VisualizationRepository,
  private val hostsRepository: HostsRepository,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
    modelClass.cast(
      PlayerViewModel(
        playerConnection = playerConnection,
        getPlayerInputUseCase = getPlayerInputUseCase,
        playlistsRepository = playlistsRepository,
        visualizationRepository = visualizationRepository,
        hostsRepository = hostsRepository,
      )
    )
}
