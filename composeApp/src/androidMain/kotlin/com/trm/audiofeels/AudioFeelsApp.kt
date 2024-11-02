package com.trm.audiofeels

import android.app.Application
import com.trm.audiofeels.di.AndroidApplicationComponent
import com.trm.audiofeels.di.create

class AudioFeelsApp : Application() {
  private lateinit var applicationComponent: AndroidApplicationComponent

  override fun onCreate() {
    super.onCreate()
    applicationComponent = AndroidApplicationComponent.create(this)
  }
}
