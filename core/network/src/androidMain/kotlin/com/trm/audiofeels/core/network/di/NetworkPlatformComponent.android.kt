package com.trm.audiofeels.core.network.di

import android.app.Application
import me.tatarka.inject.annotations.Provides

actual interface NetworkPlatformComponent {
  @Provides
  fun provideCoilPlatformContext(application: Application): coil3.PlatformContext = application
}
