package com.trm.audiofeels.di

import android.app.Application
import android.content.Context
import android.content.Intent
import com.trm.audiofeels.MainActivity
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.di.MainActivityIntent
import com.trm.audiofeels.core.base.util.BuildInfo
import com.trm.audiofeels.core.base.util.PlatformContext
import korlibs.korlibs_platform.BuildConfig
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class AndroidApplicationComponent(@get:Provides val application: Application) :
  ApplicationComponent {
  @Provides fun bindApplicationPlatformContext(): PlatformContext = application

  @Provides
  fun mainActivityIntent(): @MainActivityIntent Intent =
    Intent(application, MainActivity::class.java)

  @Provides fun buildInfo(): BuildInfo = BuildInfo(debug = BuildConfig.DEBUG)

  companion object
}

val Context.applicationComponent: ApplicationComponent
  get() = (applicationContext as ApplicationComponentProvider).component
