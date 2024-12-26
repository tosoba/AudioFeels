package com.trm.audiofeels.core.player.di

import android.app.Service

interface PlayerServiceComponentFactory {
  fun create(service: Service): PlayerServiceComponent
}
