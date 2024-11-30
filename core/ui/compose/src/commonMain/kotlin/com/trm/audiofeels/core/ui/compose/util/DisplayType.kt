package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

enum class DisplayType {
  SINGLE_PANE,
  DUAL_PANE,
}

@Composable
fun calculateDisplayType(displayFeaturesManager: DisplayManager): DisplayType =
  when (displayFeaturesManager.windowSizeClass().widthSizeClass) {
    WindowWidthSizeClass.Compact -> {
      DisplayType.SINGLE_PANE
    }
    WindowWidthSizeClass.Medium -> {
      if (!displayFeaturesManager.isNormalDevicePosture()) {
        DisplayType.DUAL_PANE
      } else {
        DisplayType.SINGLE_PANE
      }
    }
    WindowWidthSizeClass.Expanded -> {
      DisplayType.DUAL_PANE
    }
    else -> {
      DisplayType.SINGLE_PANE
    }
  }
