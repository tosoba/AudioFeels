package com.trm.audiofeels.data.hosts.di

import com.trm.audiofeels.core.network.HostFetcher
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.data.hosts.HostsRepository
import me.tatarka.inject.annotations.Provides

interface HostsComponent {
  @Provides fun bindHostRetriever(repository: HostsRepository): HostRetriever

  @Provides fun bindHostFetcher(repository: HostsRepository): HostFetcher
}
