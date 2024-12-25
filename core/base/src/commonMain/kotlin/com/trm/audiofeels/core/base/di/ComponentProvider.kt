package com.trm.audiofeels.core.base.di

import kotlin.reflect.KClass

interface ComponentProvider {
  fun <T : Any> provideComponent(`class`: KClass<T>): T
}
