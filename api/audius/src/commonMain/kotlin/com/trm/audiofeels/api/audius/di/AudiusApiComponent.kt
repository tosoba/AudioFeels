package com.trm.audiofeels.api.audius.di

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.BuildInfo
import com.trm.audiofeels.core.network.host.HostFetcher
import com.trm.audiofeels.core.network.host.HostRetriever
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

interface AudiusApiComponent {
  @ApplicationScope
  @Provides
  fun audiusEndpoints(
    cacheStorage: CacheStorage,
    hostRetriever: HostRetriever,
    hostFetcher: HostFetcher,
    buildInfo: BuildInfo,
  ): AudiusEndpoints =
    AudiusEndpoints(
      hostRetriever = hostRetriever,
      hostFetcher = hostFetcher,
      logLevel = if (buildInfo.debug) LogLevel.ALL else LogLevel.NONE,
      cacheStorage = cacheStorage,
    )
}
