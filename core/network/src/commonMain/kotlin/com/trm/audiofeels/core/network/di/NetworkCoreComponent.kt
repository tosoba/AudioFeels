package com.trm.audiofeels.core.network.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.network.host.HostValidator
import com.trm.audiofeels.core.network.monitor.NetworkMonitor
import com.trm.audiofeels.core.network.monitor.NetworkPlatformMonitor
import me.tatarka.inject.annotations.Provides

interface NetworkCoreComponent {
  @Provides fun hostValidator(): HostValidator = HostValidator()

  @Provides
  @ApplicationScope
  fun networkMonitor(platformContext: PlatformContext): NetworkMonitor =
    NetworkPlatformMonitor(platformContext = platformContext)
}
