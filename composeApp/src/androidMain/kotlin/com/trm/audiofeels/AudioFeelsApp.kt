package com.trm.audiofeels

import android.app.Application
import com.trm.audiofeels.core.base.di.AndroidComponentProvider
import com.trm.audiofeels.di.AndroidApplicationComponent
import com.trm.audiofeels.di.ApplicationComponentProvider
import com.trm.audiofeels.di.create

class AudioFeelsApp : Application(), ApplicationComponentProvider, AndroidComponentProvider {
  override lateinit var component: AndroidApplicationComponent

  override fun onCreate() {
    super.onCreate()
    component = AndroidApplicationComponent.create(this)
  }

  override fun <T> provideComponent(clazz: Class<T>): T =
    requireNotNull(clazz.cast(component)) { "Invalid component type." }
}
