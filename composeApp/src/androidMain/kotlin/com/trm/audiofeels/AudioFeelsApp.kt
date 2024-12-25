package com.trm.audiofeels

import android.app.Application
import android.app.Service
import com.trm.audiofeels.core.base.di.ServiceComponentFactory
import com.trm.audiofeels.di.AndroidApplicationComponent
import com.trm.audiofeels.di.ApplicationComponentProvider
import com.trm.audiofeels.di.ServiceComponent
import com.trm.audiofeels.di.create
import kotlin.reflect.KClass
import kotlin.reflect.cast

class AudioFeelsApp : Application(), ApplicationComponentProvider, ServiceComponentFactory {
  override lateinit var component: AndroidApplicationComponent

  override fun onCreate() {
    super.onCreate()
    component = AndroidApplicationComponent.create(this)
  }

  override fun <T : Any> create(service: Service, `class`: KClass<T>): T =
    `class`.cast(ServiceComponent.create(service, component))
}
