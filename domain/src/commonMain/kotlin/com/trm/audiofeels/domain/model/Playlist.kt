package com.trm.audiofeels.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
  val id: String,
  val name: String,
  val description: String?,
  val artworkUrl: String?,
  val score: Double,
  val trackCount: Int,
)
