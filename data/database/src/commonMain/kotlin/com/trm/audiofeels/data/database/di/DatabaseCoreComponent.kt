package com.trm.audiofeels.data.database.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.data.database.ApplicationDatabase
import com.trm.audiofeels.data.database.createApplicationDatabase
import com.trm.audiofeels.data.database.dao.PlaylistDao
import com.trm.audiofeels.data.database.dao.SuggestionDao
import me.tatarka.inject.annotations.Provides

interface DatabaseCoreComponent {
  @Provides
  @ApplicationScope
  fun applicationDatabase(platformContext: PlatformContext): ApplicationDatabase =
    createApplicationDatabase(platformContext)

  @Provides
  @ApplicationScope
  fun playlistDao(database: ApplicationDatabase): PlaylistDao = database.playlistDao()

  @Provides
  @ApplicationScope
  fun suggestionDao(database: ApplicationDatabase): SuggestionDao = database.suggestionDao()
}
