package com.trm.audiofeels.di

import android.app.Application
import com.trm.audiofeels.base.di.ApplicationScope
import com.trm.audiofeels.core.cache.di.CacheComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class AndroidApplicationComponent(@get:Provides val application: Application) :
  CacheComponent {
  companion object
}
