package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType

enum class NavigationType {
  BOTTOM_NAVIGATION,
  NAVIGATION_RAIL,
  PERMANENT_NAVIGATION_DRAWER;

  companion object {
    operator fun invoke(suiteType: NavigationSuiteType): NavigationType =
      when (suiteType) {
        NavigationSuiteType.NavigationBar -> BOTTOM_NAVIGATION
        NavigationSuiteType.NavigationRail -> NAVIGATION_RAIL
        NavigationSuiteType.NavigationDrawer -> PERMANENT_NAVIGATION_DRAWER
        else -> BOTTOM_NAVIGATION
      }
  }
}
