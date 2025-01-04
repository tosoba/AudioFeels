package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlin.reflect.KClass
import kotlin.reflect.cast
import me.tatarka.inject.annotations.Inject

@Inject
actual class PlayerViewModelFactory(
  private val playerConnection: PlayerConnection,
  private val playlistsRepository: PlaylistsRepository,
  private val hostRetriever: HostRetriever,
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
    modelClass.cast(
      PlayerViewModel(
        savedPlaylist = null,
        savePlaylist = {},
        playerConnection = playerConnection,
        repository = playlistsRepository,
        hostRetriever = hostRetriever,
      )
    )
}
