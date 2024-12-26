package com.trm.audiofeels

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.trm.audiofeels.core.player.PlayerService
import com.trm.audiofeels.core.player.di.BasePlayerServiceComponent
import com.trm.audiofeels.core.player.di.PlayerServiceComponentFactory
import com.trm.audiofeels.di.AndroidApplicationComponent
import com.trm.audiofeels.di.ApplicationComponentProvider
import com.trm.audiofeels.di.PlayerServiceComponent
import com.trm.audiofeels.di.create

class AudioFeelsApp : Application(), ApplicationComponentProvider, PlayerServiceComponentFactory {
  override lateinit var component: AndroidApplicationComponent

  override fun onCreate() {
    super.onCreate()
    component = AndroidApplicationComponent.create(this)
  }

  override fun create(
    @OptIn(UnstableApi::class) service: PlayerService
  ): BasePlayerServiceComponent = PlayerServiceComponent.create(service, component)
}
