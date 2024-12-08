package com.trm.audiofeels.core.player

import android.content.Context
import com.trm.audiofeels.core.base.util.ApplicationCoroutineScope
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.core.player.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

actual class PlayerPlatformConnection(
  private val context: Context,
  private val hostRetriever: HostRetriever,
  private val scope: ApplicationCoroutineScope,
) : PlayerConnection {
  override val playerStateFlow: StateFlow<PlayerState>
    get() = TODO("Not yet implemented")

  override val currentPositionMsFlow: StateFlow<Long>
    get() = TODO("Not yet implemented")

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
