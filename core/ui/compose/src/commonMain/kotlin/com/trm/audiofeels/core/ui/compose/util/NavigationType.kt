package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass

enum class NavigationType(val suiteType: NavigationSuiteType) {
  NAVIGATION_BAR(NavigationSuiteType.NavigationBar),
  NAVIGATION_RAIL(NavigationSuiteType.NavigationRail),
  PERMANENT_NAVIGATION_DRAWER(NavigationSuiteType.NavigationDrawer);

  companion object {
    operator fun invoke(adaptiveInfo: WindowAdaptiveInfo, windowSize: DpSize): NavigationType =
      when {
        adaptiveInfo.windowPosture.isTabletop ||
          adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT -> {
          NAVIGATION_BAR
        }
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED &&
          windowSize.width >= 1200.dp -> {
          PERMANENT_NAVIGATION_DRAWER
        }
        else -> {
          NAVIGATION_RAIL
        }
      }
  }
}
