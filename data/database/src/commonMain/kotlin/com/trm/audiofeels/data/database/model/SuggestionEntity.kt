package com.trm.audiofeels.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trm.audiofeels.data.database.converter.InstantConverter
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
@Entity(tableName = "suggestion")
data class SuggestionEntity(
  @PrimaryKey val query: String,
  @TypeConverters(InstantConverter::class) val lastSearched: Instant = Clock.System.now(),
)
