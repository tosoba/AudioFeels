package com.trm.audiofeels.api.hosts.di

import com.trm.audiofeels.api.hosts.HostsEndpoint
import com.trm.audiofeels.core.network.configureDefault
import com.trm.audiofeels.core.network.httpClient
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

interface HostsApiComponent {
  @Provides
  fun hostsEndpoint(): HostsEndpoint =
    HostsEndpoint(httpClient { configureDefault(logLevel = LogLevel.ALL, maxRetries = 2) })
}
