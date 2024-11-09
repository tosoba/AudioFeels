package com.trm.audiofeels.data.hosts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import me.tatarka.inject.annotations.Inject

// TODO: api source
// TODO: in memory source
@Inject
class HostsRepository(
  private val dataStore: Lazy<DataStore<Preferences>>,
  private val inMemoryDataSource: HostsInMemoryDataSource,
) {
  // TODO: pass reference to those methods to HostInterceptor
  suspend fun getAudiusHost(): String {
    return ""
  }

  suspend fun updateAudiusHost() {}
}
