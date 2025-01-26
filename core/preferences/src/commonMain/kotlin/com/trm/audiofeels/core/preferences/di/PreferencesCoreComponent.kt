package com.trm.audiofeels.core.preferences.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.preferences.dataStorePath
import me.tatarka.inject.annotations.Provides
import okio.Path.Companion.toPath

interface PreferencesCoreComponent {
  @Provides
  @ApplicationScope
  fun dataStore(platformContext: PlatformContext): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
      produceFile = { platformContext.dataStorePath.toPath() }
    )
}
