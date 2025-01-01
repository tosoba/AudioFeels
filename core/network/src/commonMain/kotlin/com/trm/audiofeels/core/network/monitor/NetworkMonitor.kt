package com.trm.audiofeels.core.network.monitor

import com.trm.audiofeels.core.base.util.PlatformContext
import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
  val connectivity: Flow<NetworkStatus>
}

expect class NetworkPlatformMonitor(platformContext: PlatformContext) : NetworkMonitor
