package com.trm.audiofeels.data.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.base.util.documentDirectoryPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual fun createApplicationDatabase(
  platformContext: PlatformContext
): ApplicationDatabase =
  Room.databaseBuilder<ApplicationDatabase>(name = "$documentDirectoryPath/$dbFileName")
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

actual fun createApplicationInMemoryDatabase(
  platformContext: PlatformContext
): ApplicationDatabase = Room.inMemoryDatabaseBuilder<ApplicationDatabase>().build()
