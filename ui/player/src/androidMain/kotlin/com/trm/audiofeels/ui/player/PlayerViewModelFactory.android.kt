package com.trm.audiofeels.ui.player

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import me.tatarka.inject.annotations.Inject

@Inject
actual class PlayerViewModelFactory(
  private val playerConnection: PlayerConnection,
  private val playlistsRepository: PlaylistsRepository,
  private val hostRetriever: HostRetriever,
) : AbstractSavedStateViewModelFactory(), ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(
    key: String,
    modelClass: Class<T>,
    handle: SavedStateHandle,
  ): T =
    PlayerViewModel(
      savedPlaylist = handle.getPlaylist(),
      savePlaylist = handle::setPlaylist,
      playerConnection = playerConnection,
      repository = playlistsRepository,
      hostRetriever = hostRetriever,
    )
      as T
}
