package com.trm.audiofeels.data.test

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

class FakeDataStorePreferences(initialValue: Preferences = mutablePreferencesOf()) :
  DataStore<Preferences> {
  private val preferences = MutableStateFlow(initialValue)

  override val data: Flow<Preferences>
    get() = preferences

  override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
    preferences.updateAndGet { transform(it) }
}
