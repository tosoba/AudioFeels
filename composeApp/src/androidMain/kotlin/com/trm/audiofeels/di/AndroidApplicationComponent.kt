package com.trm.audiofeels.di

import android.app.Application
import com.trm.audiofeels.core.base.di.ApplicationScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class AndroidApplicationComponent(@get:Provides val application: Application) :
  ApplicationComponent {
  companion object
}
