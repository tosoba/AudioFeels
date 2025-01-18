package com.trm.audiofeels.ui.player

import androidx.compose.ui.graphics.ImageBitmap
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist

data class PlayerViewState(
  val isVisible: Boolean,
  val playlist: Playlist?,
  val playerState: PlayerState,
  val currentTrackProgress: Double,
  val playerInput: LoadableState<PlayerInput>,
  val trackImageBitmap: ImageBitmap?, // TODO: use a placeholder in case of no artwork
)
