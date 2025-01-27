package com.trm.audiofeels.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "playlist")
data class PlaylistEntity(
  @PrimaryKey val id: String,
  val name: String,
  val description: String?,
  val artworkUrl: String?,
  val score: Double,
  val trackCount: Int,
)
