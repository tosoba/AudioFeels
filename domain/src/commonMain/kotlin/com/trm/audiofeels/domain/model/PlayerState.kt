package com.trm.audiofeels.domain.model

sealed interface PlayerState {
  data object Idle : PlayerState

  data class Enqueued(
    val currentTrack: Track,
    val currentTrackIndex: Int,
    val playbackState: PlaybackState,
    val isPlaying: Boolean,
  ) : PlayerState

  data class Error(val error: PlayerError, val previousState: PlayerState) : PlayerState {
    val previousEnqueuedState: Enqueued?
      get() = previousState as? Enqueued
  }
}
