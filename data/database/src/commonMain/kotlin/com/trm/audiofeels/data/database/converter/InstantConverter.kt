package com.trm.audiofeels.data.database.converter

import androidx.room.TypeConverter
import kotlin.time.Instant

class InstantConverter {
  @TypeConverter fun fromInstant(instant: Instant): String = instant.toString()

  @TypeConverter fun toInstant(timestamp: String): Instant = Instant.parse(timestamp)
}
