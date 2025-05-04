package com.trm.audiofeels.core.test

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

class InMemoryDataStorePreferences(initialValue: Preferences) : DataStore<Preferences> {
  constructor(
    vararg initialValues: Preferences.Pair<*>
  ) : this(mutablePreferencesOf(*initialValues))

  private val preferences = MutableStateFlow(initialValue)

  override val data: Flow<Preferences>
    get() = preferences

  override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
    preferences.updateAndGet { transform(it) }
}
