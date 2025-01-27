package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import coil3.ImageLoader
import coil3.PlatformContext
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.PlaybackRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import kotlin.reflect.KClass
import kotlin.reflect.cast
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerViewModelFactory(
  private val playerConnection: PlayerConnection,
  private val getPlayerInputUseCase: GetPlayerInputUseCase,
  private val playbackRepository: PlaybackRepository,
  private val playlistsRepository: PlaylistsRepository,
  private val imageLoader: ImageLoader,
  private val platformContext: PlatformContext,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
    modelClass.cast(
      PlayerViewModel(
        playerConnection = playerConnection,
        getPlayerInputUseCase = getPlayerInputUseCase,
        playbackRepository = playbackRepository,
        playlistsRepository = playlistsRepository,
        imageLoader = imageLoader,
        platformContext = platformContext,
      )
    )
}
