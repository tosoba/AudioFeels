package com.trm.audiofeels.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class IosApplicationComponent : ApplicationComponent {
  @Provides fun bindApplicationPlatformContext(): PlatformContext = PlatformContext.INSTANCE

  companion object
}
