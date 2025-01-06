package com.trm.audiofeels.ui.player

import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track

data class PlayerViewState(
  val isVisible: Boolean,
  val playerState: PlayerState,
  val tracksState: LoadableState<List<Track>>,
)
