package com.trm.audiofeels.data.hosts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.network.HostFetcher
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.data.hosts.exception.NoHostAvailableException
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class AudiusHostsRepository(
  private val inMemoryDataSource: AudiusHostsInMemoryDataSource,
  private val dataStore: DataStore<Preferences>,
  endpoints: Lazy<HostsEndpoints>,
) : HostRetriever, HostFetcher {
  private val endpoints by endpoints

  private val mutex = Mutex()

  override suspend fun retrieveHost(): String =
    inMemoryDataSource.host
      ?: mutex.withLock {
        getHostFromPreferences()?.also(::storeHostInMemory)
          ?: fetchHosts()?.firstSuccessfulOrNull()?.also { storeHost(it.trimHttps()) }
          ?: throw NoHostAvailableException
      }

  override suspend fun fetchHost(oldHost: String): String =
    mutex.withLock {
      // Firstly, check lastWorkingHost stored in memory again (must be within the lock)
      // in case another request already fetched and updated a new working host
      // after it encountered a 404 response with the oldHost.
      inMemoryDataSource.host?.takeIf { it != oldHost }
        ?: fetchHosts()?.filter { it != oldHost }?.firstSuccessfulOrNull()?.also { storeHost(it) }
        ?: throw NoHostAvailableException
    }

  private suspend fun getHostFromPreferences() =
    dataStore.data.map { preferences -> preferences[HOST_PREF_KEY] }.firstOrNull()

  private suspend fun fetchHosts(): List<String>? = endpoints.getHosts().hosts

  private suspend fun storeHost(host: String) {
    dataStore.edit { it[HOST_PREF_KEY] = host }
    storeHostInMemory(host)
  }

  private fun storeHostInMemory(host: String) {
    inMemoryDataSource.host = host
  }

  private suspend fun List<String>.firstSuccessfulOrNull(): String? = firstOrNull {
    endpoints.pingHost(it)
  }

  private fun String.trimHttps() = replace("https://", "")

  companion object {
    val HOST_PREF_KEY = stringPreferencesKey("host")
  }
}
