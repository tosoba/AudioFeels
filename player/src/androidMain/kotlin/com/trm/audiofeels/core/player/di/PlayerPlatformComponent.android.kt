package com.trm.audiofeels.core.player.di

import android.app.Application
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.core.player.PlayerPlatformConnection
import me.tatarka.inject.annotations.Provides

actual interface PlayerPlatformComponent {
  @Provides
  @ApplicationScope
  fun playerConnection(application: Application): PlayerConnection =
    PlayerPlatformConnection(context = application)
}
