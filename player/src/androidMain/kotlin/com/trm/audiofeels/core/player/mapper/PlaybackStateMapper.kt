package com.trm.audiofeels.core.player.mapper

import androidx.media3.common.Player
import com.trm.audiofeels.domain.model.PlaybackState

internal fun enumPlaybackStateOf(@Player.State state: Int): PlaybackState =
  when (state) {
    Player.STATE_IDLE -> PlaybackState.IDLE
    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
    Player.STATE_READY -> PlaybackState.READY
    Player.STATE_ENDED -> PlaybackState.ENDED
    else -> throw IllegalArgumentException()
  }
