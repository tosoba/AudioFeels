package com.trm.audiofeels.data.hosts.di

import com.trm.audiofeels.core.network.host.HostFetcher
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.data.hosts.AudiusHostsRepository
import com.trm.audiofeels.domain.repository.HostsRepository
import me.tatarka.inject.annotations.Provides

interface HostsDataComponent {
  @Provides fun bindHostsRepository(repository: AudiusHostsRepository): HostsRepository = repository

  @Provides fun bindHostRetriever(repository: AudiusHostsRepository): HostRetriever = repository

  @Provides fun bindHostFetcher(repository: AudiusHostsRepository): HostFetcher = repository
}
