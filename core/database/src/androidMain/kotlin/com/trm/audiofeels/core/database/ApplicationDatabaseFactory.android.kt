package com.trm.audiofeels.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.trm.audiofeels.core.base.util.PlatformContext
import kotlinx.coroutines.Dispatchers

internal actual fun createApplicationDatabase(
  platformContext: PlatformContext
): ApplicationDatabase =
  Room.databaseBuilder<ApplicationDatabase>(
      context = platformContext,
      name = platformContext.getDatabasePath(dbFileName).absolutePath,
    )
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
