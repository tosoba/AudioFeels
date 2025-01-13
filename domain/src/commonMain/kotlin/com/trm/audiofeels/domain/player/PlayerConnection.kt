package com.trm.audiofeels.domain.player

import com.trm.audiofeels.domain.model.PlayerConstants
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

  fun skipTo(trackIndex: Int, positionMs: Long = 0L)

  fun enqueue(
    tracks: List<Track>,
    host: String,
    autoPlay: Boolean = true,
    startTrackIndex: Int = PlayerConstants.DEFAULT_START_TRACK_INDEX,
    startPositionMs: Long = PlayerConstants.DEFAULT_START_POSITION_MS,
  )

  fun reset()
}
