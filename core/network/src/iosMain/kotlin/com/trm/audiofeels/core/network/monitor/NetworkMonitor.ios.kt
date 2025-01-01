package com.trm.audiofeels.core.network.monitor

import com.trm.audiofeels.core.base.util.PlatformContext
import kotlinx.coroutines.flow.Flow

actual class NetworkPlatformMonitor actual constructor(platformContext: PlatformContext) :
  NetworkMonitor {
  override val connectivity: Flow<NetworkStatus>
    get() = TODO("Not yet implemented")
}
