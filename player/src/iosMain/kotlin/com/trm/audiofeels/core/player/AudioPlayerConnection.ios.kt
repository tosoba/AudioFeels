package com.trm.audiofeels.core.player

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
actual class AudioPlayerConnection : PlayerConnection {
  private val _playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Idle)
  override val playerState: StateFlow<PlayerState> = _playerStateFlow.asStateFlow()

  override val currentPositionMs: StateFlow<Long> = MutableStateFlow(0L)

  override fun play() {
    TODO("Not yet implemented")
  }

  override fun pause() {
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

  override fun skipTo(trackIndex: Int, positionMs: Long) {
    TODO("Not yet implemented")
  }

  override fun play(
    tracks: List<Track>,
    host: String,
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
