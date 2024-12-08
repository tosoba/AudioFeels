package com.trm.audiofeels.core.player.model

import com.trm.audiofeels.domain.model.Track

sealed interface PlayerState {
  data object Idle : PlayerState

  data class Initialized(
    val currentTrack: Track,
    val currentTrackIndex: Int,
    val tracksCount: Int,
    val playbackState: PlaybackState,
    val isPlaying: Boolean,
    val trackDurationMs: Long = PlayerConstants.DEFAULT_DURATION_MS,
  ) : PlayerState
}
