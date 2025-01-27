package com.trm.audiofeels.core.database.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.database.ApplicationDatabase
import com.trm.audiofeels.core.database.createApplicationDatabase
import com.trm.audiofeels.core.database.dao.PlaylistDao
import me.tatarka.inject.annotations.Provides

interface DatabaseCoreComponent {
  @Provides
  @ApplicationScope
  fun applicationDatabase(platformContext: PlatformContext): ApplicationDatabase =
    createApplicationDatabase(platformContext)

  @Provides
  @ApplicationScope
  fun playlistDao(database: ApplicationDatabase): PlaylistDao = database.playlistDao()
}
