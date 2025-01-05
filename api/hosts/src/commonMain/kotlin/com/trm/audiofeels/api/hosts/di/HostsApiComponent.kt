package com.trm.audiofeels.api.hosts.di

import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.BuildInfo
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

interface HostsApiComponent {
  @ApplicationScope
  @Provides
  fun hostsEndpoint(buildInfo: BuildInfo): HostsEndpoints =
    HostsEndpoints(logLevel = if (buildInfo.debug) LogLevel.ALL else LogLevel.NONE)
}
