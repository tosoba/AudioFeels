package com.trm.audiofeels.di

import android.app.Application
import android.content.Context
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class AndroidApplicationComponent(@get:Provides val application: Application) :
  ApplicationComponent {
  @Provides fun bindApplicationPlatformContext(): PlatformContext = application

  companion object
}

val Context.applicationComponent: ApplicationComponent
  get() = (applicationContext as ApplicationComponentProvider).component
