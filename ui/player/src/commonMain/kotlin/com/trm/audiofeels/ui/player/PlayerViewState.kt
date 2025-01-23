package com.trm.audiofeels.ui.player

import androidx.compose.ui.graphics.ImageBitmap
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

sealed interface PlayerViewState {
  val playerVisible: Boolean
  val currentTrackImageBitmap: ImageBitmap?
  val actions: PlayerViewActions

  data object Idle : PlayerViewState {
    override val playerVisible: Boolean = false
    override val currentTrackImageBitmap: ImageBitmap? = null
    override val actions: PlayerViewActions = object : PlayerViewActions {}
  }

  data class Playback(
    val playlist: Playlist,
    val playerState: PlayerState = PlayerState.Idle,
    val tracks: LoadableState<List<Track>> = LoadableState.Loading,
    val currentTrackProgress: Double = 0.0,
    override val currentTrackImageBitmap: ImageBitmap? =
      null, // TODO: use a placeholder in case of no artwork
    override val actions: PlayerViewActions,
  ) : PlayerViewState {
    override val playerVisible: Boolean = true
  }
}
