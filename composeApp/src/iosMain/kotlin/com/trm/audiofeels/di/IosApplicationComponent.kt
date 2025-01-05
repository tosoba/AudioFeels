package com.trm.audiofeels.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.BuildInfo
import com.trm.audiofeels.core.base.util.PlatformContext
import kotlin.experimental.ExperimentalNativeApi
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class IosApplicationComponent : ApplicationComponent {
  @Provides fun bindApplicationPlatformContext(): PlatformContext = PlatformContext.INSTANCE

  @OptIn(ExperimentalNativeApi::class)
  @Provides
  fun buildInfo(): BuildInfo = BuildInfo(debug = Platform.isDebugBinary)

  companion object
}
