package com.trm.audiofeels.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.trm.audiofeels.core.database.dao.PlaylistDao
import com.trm.audiofeels.core.database.model.PlaylistEntity

@Database(entities = [PlaylistEntity::class], version = 1)
@ConstructedBy(ApplicationDatabaseConstructor::class)
abstract class ApplicationDatabase : RoomDatabase() {
  abstract fun playlistDao(): PlaylistDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ApplicationDatabaseConstructor : RoomDatabaseConstructor<ApplicationDatabase> {
  override fun initialize(): ApplicationDatabase
}
