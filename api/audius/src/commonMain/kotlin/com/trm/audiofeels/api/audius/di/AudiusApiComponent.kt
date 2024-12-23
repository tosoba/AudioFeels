package com.trm.audiofeels.api.audius.di

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.core.network.host.HostFetcher
import com.trm.audiofeels.core.network.host.HostRetriever
import io.ktor.client.plugins.cache.storage.CacheStorage
import me.tatarka.inject.annotations.Provides

interface AudiusApiComponent {
  @Provides
  fun audiusEndpoints(
    cacheStorage: CacheStorage,
    hostRetriever: HostRetriever,
    hostFetcher: HostFetcher,
  ): AudiusEndpoints =
    AudiusEndpoints(
      hostRetriever = hostRetriever,
      hostFetcher = hostFetcher,
      cacheStorage = cacheStorage,
    )
}
