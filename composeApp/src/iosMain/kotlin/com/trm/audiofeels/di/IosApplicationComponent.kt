package com.trm.audiofeels.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import me.tatarka.inject.annotations.Component

@ApplicationScope
@Component
abstract class IosApplicationComponent : ApplicationComponent {
  companion object
}
