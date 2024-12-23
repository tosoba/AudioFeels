package com.trm.audiofeels.data.hosts.di

import com.trm.audiofeels.core.network.host.HostFetcher
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.data.hosts.AudiusHostsRepository
import me.tatarka.inject.annotations.Provides

interface HostsDataComponent {
  @Provides fun bindHostRetriever(repository: AudiusHostsRepository): HostRetriever = repository

  @Provides fun bindHostFetcher(repository: AudiusHostsRepository): HostFetcher = repository
}
