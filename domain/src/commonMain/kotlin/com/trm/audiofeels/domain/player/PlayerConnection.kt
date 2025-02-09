package com.trm.audiofeels.domain.player

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

  fun enqueue(tracks: List<Track>, host: String, startTrackIndex: Int, startPositionMs: Long)

  fun reset()
}
