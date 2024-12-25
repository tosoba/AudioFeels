package com.trm.audiofeels

import android.app.Application
import com.trm.audiofeels.core.base.di.ComponentProvider
import com.trm.audiofeels.di.AndroidApplicationComponent
import com.trm.audiofeels.di.ApplicationComponentProvider
import com.trm.audiofeels.di.create
import kotlin.reflect.KClass
import kotlin.reflect.cast

class AudioFeelsApp : Application(), ApplicationComponentProvider, ComponentProvider {
  override lateinit var component: AndroidApplicationComponent

  override fun onCreate() {
    super.onCreate()
    component = AndroidApplicationComponent.create(this)
  }

  override fun <T : Any> provideComponent(`class`: KClass<T>): T =
    requireNotNull(`class`.cast(component)) { "Invalid component type." }
}
