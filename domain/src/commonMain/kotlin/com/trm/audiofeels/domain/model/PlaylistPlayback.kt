package com.trm.audiofeels.domain.model

data class PlaylistPlayback(
  val playlist: Playlist,
  val currentTrackIndex: Int,
  val currentTrackPositionMs: Long,
  val autoPlay: Boolean
)
