package com.trm.audiofeels.domain.player

import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlayerConnection {
  val playerState: Flow<PlayerState>

  val currentTrackPositionMs: Flow<Long>

  fun play()

  fun pause()

  fun playPrevious()

  fun playNext()

  fun skipTo(positionMs: Long)

  fun enqueue(input: PlayerInput, startTrackIndex: Int, startPositionMs: Long)

  fun reset()
}
