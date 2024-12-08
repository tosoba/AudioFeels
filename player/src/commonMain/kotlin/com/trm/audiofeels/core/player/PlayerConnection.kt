package com.trm.audiofeels.core.player

import com.trm.audiofeels.core.player.model.PlayerConstants
import com.trm.audiofeels.core.player.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerConnection {
  val playerStateFlow: StateFlow<PlayerState>

  val currentPositionMsFlow: StateFlow<Long>

  fun toggleIsPlaying()

  fun playPrevious()

  fun playNext()

  fun skipTo(positionMs: Long)

  fun skipTo(itemIndex: Int, positionMs: Long = 0L)

  fun play(
    tracks: List<Track>,
    autoPlay: Boolean = true,
    startIndex: Int = PlayerConstants.DEFAULT_START_INDEX,
    startPositionMs: Long = PlayerConstants.DEFAULT_START_POSITION_MS,
  )

  fun reset()
}

expect class PlayerPlatformConnection : PlayerConnection
