package com.trm.audiofeels.api.hosts.di

import com.trm.audiofeels.api.hosts.HostsEndpoints
import me.tatarka.inject.annotations.Provides

interface HostsApiComponent {
  @Provides fun hostsEndpoint(): HostsEndpoints = HostsEndpoints()
}
