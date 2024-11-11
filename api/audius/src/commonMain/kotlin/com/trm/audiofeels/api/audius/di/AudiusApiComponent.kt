package com.trm.audiofeels.api.audius.di

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.core.network.HostFetcher
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.core.network.configureDefault
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

interface AudiusApiComponent {
  @Provides
  fun audiusEndpoints(
    cacheStorage: CacheStorage,
    hostRetriever: HostRetriever,
    hostFetcher: HostFetcher,
  ): AudiusEndpoints =
    AudiusEndpoints(hostRetriever = hostRetriever, hostFetcher = hostFetcher) {
      configureDefault(logLevel = LogLevel.ALL, cacheStorage = cacheStorage, maxRetries = 2)
    }
}
