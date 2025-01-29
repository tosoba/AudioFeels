package com.trm.audiofeels.data.hosts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.trimHttps
import com.trm.audiofeels.core.network.host.HostFetcher
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.network.host.HostValidator
import com.trm.audiofeels.core.preferences.get
import com.trm.audiofeels.core.preferences.set
import com.trm.audiofeels.data.hosts.exception.NoHostAvailableException
import com.trm.audiofeels.domain.repository.HostsRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class AudiusHostsRepository(
  private val inMemoryDataSource: AudiusHostsInMemoryDataSource,
  private val dataStore: DataStore<Preferences>,
  endpoints: Lazy<HostsEndpoints>,
  validator: Lazy<HostValidator>,
) : HostsRepository, HostRetriever, HostFetcher {
  private val endpoints by endpoints
  private val validator by validator

  private val mutex = Mutex()

  override suspend fun retrieveHost(): String =
    inMemoryDataSource.host
      ?: mutex.withLock {
        inMemoryDataSource.host
          ?: dataStore.get(hostPreferenceKey)?.also(::storeHostInMemory)
          ?: fetchHosts().findAndStoreFirstValidOrThrow()
      }

  override suspend fun fetchHost(oldHost: String): String =
    mutex.withLock {
      // Firstly, check lastWorkingHost stored in memory again (must be within the lock)
      // in case another request already fetched and updated a new working host
      // after it encountered a 404 response with the oldHost.
      inMemoryDataSource.host?.takeIf { it != oldHost }
        ?: fetchHosts()?.filter { it != oldHost }.findAndStoreFirstValidOrThrow()
    }

  override suspend fun fetchHost(): String =
    mutex.withLock { fetchHosts().findAndStoreFirstValidOrThrow() }

  private suspend fun fetchHosts(): List<String>? = endpoints.getHosts().hosts

  private suspend fun storeHost(host: String) {
    val trimmed = host.trimHttps()
    dataStore.set(hostPreferenceKey, trimmed)
    storeHostInMemory(trimmed)
  }

  private fun storeHostInMemory(host: String) {
    inMemoryDataSource.host = host
  }

  private suspend fun List<String>?.findAndStoreFirstValidOrThrow(): String =
    this?.firstOrNull { validator.isValid(it) }?.also { storeHost(it) }
      ?: throw NoHostAvailableException

  companion object {
    val hostPreferenceKey = stringPreferencesKey("host")
  }
}
