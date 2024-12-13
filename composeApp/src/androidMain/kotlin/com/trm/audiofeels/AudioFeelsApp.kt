package com.trm.audiofeels

import android.app.Application
import com.trm.audiofeels.di.AndroidApplicationComponent
import com.trm.audiofeels.di.ApplicationComponentProvider
import com.trm.audiofeels.di.create

class AudioFeelsApp : Application(), ApplicationComponentProvider {
  override lateinit var component: AndroidApplicationComponent

  override fun onCreate() {
    super.onCreate()
    component = AndroidApplicationComponent.create(this)
  }
}
