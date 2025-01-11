package com.trm.audiofeels.ui.player

import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.PlayerState

data class PlayerViewState(
  val isVisible: Boolean,
  val playerState: PlayerState,
  val playerInput: LoadableState<PlayerInput>,
)
