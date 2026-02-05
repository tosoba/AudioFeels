package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

enum class NavigationType(val suiteType: NavigationSuiteType) {
  NAVIGATION_BAR(NavigationSuiteType.NavigationBar),
  NAVIGATION_RAIL(NavigationSuiteType.NavigationRail),
  PERMANENT_NAVIGATION_DRAWER(NavigationSuiteType.NavigationDrawer);

  companion object {
    @Composable
    operator fun invoke(adaptiveInfo: WindowAdaptiveInfo, windowSize: DpSize): NavigationType =
      when {
        adaptiveInfo.windowPosture.isTabletop ||
          currentWindowWidthClass() == WindowWidthSizeClass.Compact -> {
          NAVIGATION_BAR
        }
        currentWindowWidthClass() == WindowWidthSizeClass.Expanded &&
          windowSize.width >= 1200.dp -> {
          PERMANENT_NAVIGATION_DRAWER
        }
        else -> {
          NAVIGATION_RAIL
        }
      }
  }
}
