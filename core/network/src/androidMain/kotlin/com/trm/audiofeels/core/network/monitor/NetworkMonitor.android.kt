package com.trm.audiofeels.core.network.monitor

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import com.trm.audiofeels.core.base.util.PlatformContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

actual class NetworkPlatformMonitor actual constructor(platformContext: PlatformContext) :
  NetworkMonitor {
  override val connectivity: Flow<NetworkStatus> =
    callbackFlow {
        val connectivityManager = platformContext.getSystemService<ConnectivityManager>()
        val callback =
          object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
              channel.trySend(connectivityManager.isCurrentlyConnected())
            }

            override fun onLost(network: Network) {
              channel.trySend(connectivityManager.isCurrentlyConnected())
            }

            override fun onCapabilitiesChanged(
              network: Network,
              networkCapabilities: NetworkCapabilities,
            ) {
              channel.trySend(connectivityManager.isCurrentlyConnected())
            }
          }

        connectivityManager?.registerNetworkCallback(
          NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build(),
          callback,
        )

        channel.trySend(connectivityManager.isCurrentlyConnected())

        awaitClose { connectivityManager?.unregisterNetworkCallback(callback) }
      }
      .conflate()

  private fun ConnectivityManager?.isCurrentlyConnected(): NetworkStatus =
    when (this) {
      null -> {
        NetworkStatus.OFFLINE
      }
      else -> {
        val hasInternet =
          activeNetwork
            ?.let(::getNetworkCapabilities)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        if (hasInternet) {
          NetworkStatus.ONLINE
        } else {
          NetworkStatus.OFFLINE
        }
      }
    }
}
