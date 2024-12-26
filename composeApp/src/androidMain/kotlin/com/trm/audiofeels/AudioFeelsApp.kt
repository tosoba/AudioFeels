package com.trm.audiofeels

import android.app.Application
import android.app.Service
import com.trm.audiofeels.core.player.di.PlayerServiceComponent
import com.trm.audiofeels.core.player.di.PlayerServiceComponentFactory
import com.trm.audiofeels.di.AndroidApplicationComponent
import com.trm.audiofeels.di.ApplicationComponentProvider
import com.trm.audiofeels.di.ServiceComponent
import com.trm.audiofeels.di.create

class AudioFeelsApp : Application(), ApplicationComponentProvider, PlayerServiceComponentFactory {
  override lateinit var component: AndroidApplicationComponent

  override fun onCreate() {
    super.onCreate()
    component = AndroidApplicationComponent.create(this)
  }

  override fun create(service: Service): PlayerServiceComponent =
    ServiceComponent.create(service, component)
}
