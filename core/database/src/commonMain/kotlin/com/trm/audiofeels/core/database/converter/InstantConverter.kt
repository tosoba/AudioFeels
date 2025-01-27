package com.trm.audiofeels.core.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class InstantConverter {
  @TypeConverter
  fun fromInstant(instant: Instant): String = instant.toString()

  @TypeConverter
  fun toInstant(timestamp: String): Instant = Instant.parse(timestamp)
}
