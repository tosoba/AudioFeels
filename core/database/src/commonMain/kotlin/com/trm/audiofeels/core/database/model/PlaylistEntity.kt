package com.trm.audiofeels.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "playlist")
data class PlaylistEntity(
  @PrimaryKey val id: String,
  val name: String,
  val description: String?,
  @SerialName("artwork_url") val artworkUrl: String?,
  val score: Double,
  @SerialName("track_count") val trackCount: Int,
)
