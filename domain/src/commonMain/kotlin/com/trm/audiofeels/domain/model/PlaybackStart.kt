package com.trm.audiofeels.domain.model

data class PlaybackStart(
  val trackIndex: Int = 0,
  val trackPositionMs: Long = 0L,
  val autoPlay: Boolean = false,
)
