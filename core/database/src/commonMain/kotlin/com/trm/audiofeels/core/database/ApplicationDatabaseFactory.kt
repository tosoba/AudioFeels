package com.trm.audiofeels.core.database

import com.trm.audiofeels.core.base.util.PlatformContext

internal expect fun createApplicationDatabase(platformContext: PlatformContext): ApplicationDatabase

internal expect fun createApplicationInMemoryDatabase(
  platformContext: PlatformContext
): ApplicationDatabase

internal const val dbFileName = "audio_feels.room_db"
