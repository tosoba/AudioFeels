package com.trm.audiofeels.api.audius.di

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.core.network.configureDefault
import com.trm.audiofeels.core.network.httpClient
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

interface AudiusApiComponent {
  @Provides
  fun audiusEndpoints(cacheStorage: CacheStorage): AudiusEndpoints =
    AudiusEndpoints(
      httpClient {
        configureDefault(logLevel = LogLevel.ALL, cacheStorage = cacheStorage, maxRetries = 2)
      }
    )
}
