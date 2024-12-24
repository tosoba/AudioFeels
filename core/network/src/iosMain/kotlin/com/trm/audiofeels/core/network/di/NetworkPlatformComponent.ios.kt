package com.trm.audiofeels.core.network.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.network.monitor.NetworkMonitor
import com.trm.audiofeels.core.network.monitor.NetworkPlatformMonitor
import me.tatarka.inject.annotations.Provides

actual interface NetworkPlatformComponent {
  @Provides @ApplicationScope fun networkMonitor(): NetworkMonitor = NetworkPlatformMonitor()
}
