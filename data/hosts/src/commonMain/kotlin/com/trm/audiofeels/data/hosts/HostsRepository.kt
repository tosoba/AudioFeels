package com.trm.audiofeels.data.hosts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.data.hosts.exception.NoHostAvailableException
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class HostsRepository(
  private val inMemoryDataSource: HostsInMemoryDataSource,
  private val dataStore: DataStore<Preferences>,
  endpoints: Lazy<HostsEndpoints>,
) {
  private val endpoints by endpoints

  private val mutex = Mutex()

  suspend fun getHost(): String =
    inMemoryDataSource.host
      ?: mutex.withLock {
        dataStore.data
          .map { preferences -> preferences[HOST_PREF_KEY] }
          .firstOrNull()
          ?.also(::storeHostInMemory)
          ?: fetchHosts()?.firstSuccessful()?.also { storeHost(it) }
          ?: throw NoHostAvailableException
      }

  suspend fun fetchNewHost(oldHost: String?): String =
    mutex.withLock {
      // Firstly, check lastWorkingHost stored in memory again
      // in case another request already fetched and updated a new working host
      // after it encountered a 404 response with the oldHost.
      inMemoryDataSource.host?.takeIf { it != oldHost }
        ?: fetchHosts()
          ?.run { oldHost?.let { filter { host -> host != it } } ?: this }
          ?.firstSuccessful()
          ?.also { storeHost(it) }
        ?: throw NoHostAvailableException
    }

  private suspend fun fetchHosts(): List<String>? = endpoints.getHosts().hosts

  private suspend fun storeHost(host: String) {
    dataStore.edit { it[HOST_PREF_KEY] = host }
    storeHostInMemory(host)
  }

  private fun storeHostInMemory(host: String) {
    inMemoryDataSource.host = host
  }

  private suspend fun List<String>.firstSuccessful(): String? = firstOrNull {
    endpoints.pingHost(it.trimHttps())
  }

  private fun String.trimHttps() = replace("https://", "")

  companion object {
    private val HOST_PREF_KEY = stringPreferencesKey("host")
  }
}
