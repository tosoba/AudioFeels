package com.trm.audiofeels.data.hosts.di

import com.trm.audiofeels.core.network.host.HostFetcher
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.data.hosts.HostsAudiusRepository
import com.trm.audiofeels.domain.repository.HostsRepository
import me.tatarka.inject.annotations.Provides

interface HostsDataComponent {
  @Provides fun bindHostsRepository(repository: HostsAudiusRepository): HostsRepository = repository

  @Provides fun bindHostRetriever(repository: HostsAudiusRepository): HostRetriever = repository

  @Provides fun bindHostFetcher(repository: HostsAudiusRepository): HostFetcher = repository
}
