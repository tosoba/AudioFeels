package com.trm.audiofeels.ui.player

import androidx.compose.ui.graphics.ImageBitmap
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

data class PlayerViewState(
  val isVisible: Boolean = false,
  val playlist: Playlist? = null,
  val playerState: PlayerState = PlayerState.Idle,
  val tracks: LoadableState<List<Track>> = LoadableState.Loading,
  val currentTrackProgress: Double = 0.0,
  val currentTrackImageBitmap: ImageBitmap? = null, // TODO: use a placeholder in case of no artwork
  val onPlayClick: () -> Unit = {},
)
