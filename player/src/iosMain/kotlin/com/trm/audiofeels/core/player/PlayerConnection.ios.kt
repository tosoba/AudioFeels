package com.trm.audiofeels.core.player

import com.trm.audiofeels.core.player.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class PlayerPlatformConnection : PlayerConnection {
  private val _playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Idle)
  override val playerState: StateFlow<PlayerState> = _playerStateFlow.asStateFlow()

  override val currentPositionMs: StateFlow<Long> = MutableStateFlow(0L)

  override val tracks: StateFlow<List<Track>> = MutableStateFlow(emptyList())

  override fun toggleIsPlaying() {
    TODO("Not yet implemented")
  }

  override fun playPrevious() {
    TODO("Not yet implemented")
  }

  override fun playNext() {
    TODO("Not yet implemented")
  }

  override fun skipTo(positionMs: Long) {
    TODO("Not yet implemented")
  }

  override fun skipTo(itemIndex: Int, positionMs: Long) {
    TODO("Not yet implemented")
  }

  override fun play(
    tracks: List<Track>,
    autoPlay: Boolean,
    startIndex: Int,
    startPositionMs: Long,
  ) {
    TODO("Not yet implemented")
  }

  override fun reset() {
    TODO("Not yet implemented")
  }
}
