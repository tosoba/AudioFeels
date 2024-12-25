package com.trm.audiofeels.core.base.di

import android.app.Service
import kotlin.reflect.KClass

interface ServiceComponentFactory {
  fun <T : Any> create(service: Service, `class`: KClass<T>): T
}
