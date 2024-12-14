package com.trm.audiofeels.core.base.di

interface AndroidComponentProvider {
  fun <T> provideComponent(clazz: Class<T>): T
}
