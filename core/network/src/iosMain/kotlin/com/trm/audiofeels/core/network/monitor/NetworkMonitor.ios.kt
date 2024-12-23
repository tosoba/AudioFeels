package com.trm.audiofeels.core.network.monitor

import kotlinx.coroutines.flow.Flow

actual class NetworkPlatformMonitor : NetworkMonitor {
  override val connectivity: Flow<NetworkStatus>
    get() = TODO("Not yet implemented")
}
