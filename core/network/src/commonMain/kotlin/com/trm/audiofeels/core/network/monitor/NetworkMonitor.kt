package com.trm.audiofeels.core.network.monitor

import com.trm.audiofeels.core.base.util.PlatformContext
import kotlinx.coroutines.flow.Flow

expect class NetworkMonitor(platformContext: PlatformContext) {
  val connectivity: Flow<NetworkStatus>
}
