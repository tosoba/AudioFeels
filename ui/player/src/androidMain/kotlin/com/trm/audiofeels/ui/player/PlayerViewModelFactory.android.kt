package com.trm.audiofeels.ui.player

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trm.audiofeels.core.base.model.ArgumentHandle
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import me.tatarka.inject.annotations.Inject

@Inject
actual class PlayerViewModelFactory(
  private val playerConnection: PlayerConnection,
  private val playlistsRepository: PlaylistsRepository,
  private val hostsRepository: HostsRepository,
) : AbstractSavedStateViewModelFactory(), ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(
    key: String,
    modelClass: Class<T>,
    handle: SavedStateHandle,
  ): T =
    PlayerViewModel(
      playlistHandle = ArgumentHandle(savedStateHandle = handle, key = PLAYER_PLAYLIST_KEY),
      playerConnection = playerConnection,
      playlistsRepository = playlistsRepository,
      hostsRepository = hostsRepository,
    )
      as T
}
