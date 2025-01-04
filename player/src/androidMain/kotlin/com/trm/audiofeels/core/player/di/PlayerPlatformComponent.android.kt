package com.trm.audiofeels.core.player.di

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.AppCoroutineScope
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.network.monitor.NetworkMonitor
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.core.player.PlayerPlatformConnection
import me.tatarka.inject.annotations.Provides

@OptIn(UnstableApi::class)
actual interface PlayerPlatformComponent {
  @Provides
  @ApplicationScope
  fun playerConnection(
    application: Application,
    hostRetriever: HostRetriever,
    appCoroutineScope: AppCoroutineScope,
    networkMonitor: NetworkMonitor,
  ): PlayerConnection =
    PlayerPlatformConnection(
      context = application,
      scope = appCoroutineScope,
      networkMonitor = networkMonitor,
    )
}
