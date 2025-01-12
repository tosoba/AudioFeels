package com.trm.audiofeels.core.player

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
actual class AudioPlayerConnection : PlayerConnection {
  override val playerState: Flow<PlayerState> = emptyFlow()

  override val currentPositionMs: Flow<Long> = emptyFlow()

  override fun play() {}

  override fun pause() {}

  override fun playPrevious() {}

  override fun playNext() {}

  override fun skipTo(positionMs: Long) {}

  override fun skipTo(trackIndex: Int, positionMs: Long) {}

  override fun enqueue(
    tracks: List<Track>,
    host: String,
    autoPlay: Boolean,
    startTrackIndex: Int,
    startPositionMs: Long,
  ) {}

  override fun reset() {}
}
