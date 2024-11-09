package com.trm.audiofeels.core.preferences.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.preferences.DataStorePlatformPathBuilder
import com.trm.audiofeels.core.preferences.createDataStore
import me.tatarka.inject.annotations.Provides

actual interface PreferencesPlatformComponent {
  @Provides
  @ApplicationScope
  fun dataStore(): DataStore<Preferences> = createDataStore { DataStorePlatformPathBuilder()() }
}
