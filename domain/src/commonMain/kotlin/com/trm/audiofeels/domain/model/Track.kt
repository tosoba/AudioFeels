package com.trm.audiofeels.domain.model

data class Track(
  val artworkUrl: String?,
  val description: String?,
  val duration: Int,
  val genre: String?,
  val id: String,
  val mood: String?,
  val playCount: Int?,
  val tags: String?,
  val title: String,
)
