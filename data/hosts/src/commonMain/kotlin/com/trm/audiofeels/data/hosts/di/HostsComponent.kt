package com.trm.audiofeels.data.hosts.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.data.hosts.HostsInMemoryDataSource
import me.tatarka.inject.annotations.Provides

interface HostsComponent {
  @Provides
  @ApplicationScope
  fun hostsInMemoryDataSource(): HostsInMemoryDataSource = HostsInMemoryDataSource()
}
