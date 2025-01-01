package com.trm.audiofeels.core.network.di

import me.tatarka.inject.annotations.Provides

actual interface NetworkPlatformComponent {
  @Provides fun provideCoilPlatformContext(): coil3.PlatformContext = coil3.PlatformContext.INSTANCE
}
