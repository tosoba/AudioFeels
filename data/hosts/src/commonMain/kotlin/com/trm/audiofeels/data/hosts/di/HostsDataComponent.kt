package com.trm.audiofeels.data.hosts.di

import com.trm.audiofeels.core.network.HostFetcher
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.data.hosts.AudiusHostsRepository
import me.tatarka.inject.annotations.Provides

interface HostsDataComponent {
  @Provides fun bindHostRetriever(repository: AudiusHostsRepository): HostRetriever = repository

  @Provides fun bindHostFetcher(repository: AudiusHostsRepository): HostFetcher = repository
}
