package com.trm.audiofeels.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trm.audiofeels.core.database.converter.InstantConverter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "suggestion")
data class SuggestionEntity(
  @PrimaryKey val query: String,
  @TypeConverters(InstantConverter::class) val lastSearched: Instant = Clock.System.now(),
)
