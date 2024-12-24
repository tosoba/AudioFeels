package com.trm.audiofeels.core.player.di

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.AppCoroutineDispatchers
import com.trm.audiofeels.core.base.util.ApplicationCoroutineScope
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.network.monitor.NetworkMonitor
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.core.player.PlayerNotificationProvider
import com.trm.audiofeels.core.player.PlayerPlatformConnection
import me.tatarka.inject.annotations.Provides

@OptIn(UnstableApi::class)
actual interface PlayerPlatformComponent {
  val playerNotificationProvider: PlayerNotificationProvider

  @OptIn(UnstableApi::class)
  @Provides
  @ApplicationScope
  fun playerNotificationProvider(
    application: Application,
    appCoroutineDispatchers: AppCoroutineDispatchers,
  ): PlayerNotificationProvider =
    PlayerNotificationProvider(
      context = application,
      appCoroutineDispatchers = appCoroutineDispatchers,
    )

  @Provides
  @ApplicationScope
  fun playerConnection(
    application: Application,
    hostRetriever: HostRetriever,
    applicationCoroutineScope: ApplicationCoroutineScope,
    networkMonitor: NetworkMonitor,
  ): PlayerConnection =
    PlayerPlatformConnection(
      context = application,
      hostRetriever = hostRetriever,
      scope = applicationCoroutineScope,
      networkMonitor = networkMonitor,
    )
}
