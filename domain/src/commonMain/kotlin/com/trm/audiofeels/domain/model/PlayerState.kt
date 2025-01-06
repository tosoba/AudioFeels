package com.trm.audiofeels.domain.model

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

  data class Error(val error: PlayerError, val previousState: PlayerState) : PlayerState
}
