package com.trm.audiofeels.core.preferences.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.preferences.DataStorePlatformPathBuilder
import com.trm.audiofeels.core.preferences.createDataStore
import me.tatarka.inject.annotations.Provides

actual interface PreferencesPlatformComponent {
  @Provides
  @ApplicationScope
  fun dataStore(application: Application): DataStore<Preferences> =
    createDataStore { DataStorePlatformPathBuilder(application)() }
}
