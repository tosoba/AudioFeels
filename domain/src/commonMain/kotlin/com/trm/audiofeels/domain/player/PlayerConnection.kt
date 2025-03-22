package com.trm.audiofeels.domain.player

import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import kotlinx.coroutines.flow.Flow

interface PlayerConnection {
  val playerStateFlow: Flow<PlayerState>

  val currentTrackPositionMsFlow: Flow<Long>

  val audioDataFlow: Flow<List<Float>>

  fun play()

  fun pause()

  fun playPrevious()

  fun playNext()
  
  fun playAtIndex(index: Int)

  fun seekTo(positionMs: Long)

  fun enqueue(input: PlayerInput, startTrackIndex: Int, startPositionMs: Long)

  fun reset()
}
