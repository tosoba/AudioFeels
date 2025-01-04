package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaylistsRepository

class PlayerViewModel(
  playlist: Playlist?,
  private val savePlaylist: (Playlist) -> Unit,
  val playerConnection: PlayerConnection,
  private val repository: PlaylistsRepository,
) : ViewModel() {
  init {
    playlist?.let {
      // TODO: fetch tracks -> call play (without autoplay?)
    }
  }

  fun onPlaylistClick(playlist: Playlist) {
    savePlaylist(playlist)
  }
}
