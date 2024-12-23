package com.trm.audiofeels.core.network.monitor

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
  val connectivity: Flow<NetworkStatus>
}

expect class NetworkPlatformMonitor : NetworkMonitor
