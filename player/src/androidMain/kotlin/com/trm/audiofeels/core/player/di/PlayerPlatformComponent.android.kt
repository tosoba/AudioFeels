package com.trm.audiofeels.core.player.di

import android.app.Application
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.ApplicationCoroutineScope
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.core.player.PlayerPlatformConnection
import me.tatarka.inject.annotations.Provides

actual interface PlayerPlatformComponent {
  @Provides
  @ApplicationScope
  fun playerConnection(
    application: Application,
    hostRetriever: HostRetriever,
    applicationCoroutineScope: ApplicationCoroutineScope,
  ): PlayerConnection =
    PlayerPlatformConnection(
      context = application,
      hostRetriever = hostRetriever,
      scope = applicationCoroutineScope,
    )
}
