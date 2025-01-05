package com.trm.audiofeels.api.hosts.di

import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.core.base.di.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface HostsApiComponent {
  @ApplicationScope @Provides fun hostsEndpoint(): HostsEndpoints = HostsEndpoints()
}
