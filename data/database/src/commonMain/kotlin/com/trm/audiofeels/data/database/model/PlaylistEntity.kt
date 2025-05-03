package com.trm.audiofeels.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trm.audiofeels.data.database.converter.InstantConverter
import kotlinx.datetime.Instant
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
  @TypeConverters(InstantConverter::class) val lastPlayed: Instant? = null,
  val currentTrackIndex: Int = 0,
  val currentTrackPositionMs: Long = 0L,
  val autoPlay: Boolean = true,
  val favourite: Boolean = false,
)
