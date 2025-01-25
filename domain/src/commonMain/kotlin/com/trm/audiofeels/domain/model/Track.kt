package com.trm.audiofeels.domain.model

import kotlin.math.roundToLong
import kotlinx.serialization.Serializable

@Serializable
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
) {
  fun positionMsOf(progress: Double): Long = (progress * duration.toDouble() * 1000.0).roundToLong()
}
