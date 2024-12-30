package com.trm.audiofeels.core.preferences.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.preferences.createDataStore
import com.trm.audiofeels.core.preferences.dataStorePath
import me.tatarka.inject.annotations.Provides

interface PreferencesCoreComponent {
  @Provides
  @ApplicationScope
  fun dataStore(platformContext: PlatformContext): DataStore<Preferences> =
    createDataStore(platformContext::dataStorePath)
}
