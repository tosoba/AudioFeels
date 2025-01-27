package com.trm.audiofeels.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.trm.audiofeels.core.database.converter.InstantConverter
import com.trm.audiofeels.core.database.dao.PlaylistDao
import com.trm.audiofeels.core.database.model.PlaylistEntity

@Database(entities = [PlaylistEntity::class], version = 1)
@TypeConverters(InstantConverter::class)
@ConstructedBy(ApplicationDatabaseConstructor::class)
abstract class ApplicationDatabase : RoomDatabase() {
  abstract fun playlistDao(): PlaylistDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ApplicationDatabaseConstructor : RoomDatabaseConstructor<ApplicationDatabase> {
  override fun initialize(): ApplicationDatabase
}
