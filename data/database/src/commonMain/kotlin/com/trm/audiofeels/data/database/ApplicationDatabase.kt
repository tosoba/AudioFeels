package com.trm.audiofeels.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.trm.audiofeels.data.database.converter.InstantConverter
import com.trm.audiofeels.data.database.dao.PlaylistDao
import com.trm.audiofeels.data.database.dao.SuggestionDao
import com.trm.audiofeels.data.database.model.PlaylistEntity
import com.trm.audiofeels.data.database.model.SuggestionEntity

@Database(entities = [PlaylistEntity::class, SuggestionEntity::class], version = 1)
@TypeConverters(InstantConverter::class)
@ConstructedBy(ApplicationDatabaseConstructor::class)
abstract class ApplicationDatabase : RoomDatabase() {
  abstract fun playlistDao(): PlaylistDao

  abstract fun suggestionDao(): SuggestionDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ApplicationDatabaseConstructor : RoomDatabaseConstructor<ApplicationDatabase> {
  override fun initialize(): ApplicationDatabase
}
