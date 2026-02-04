package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass

enum class NavigationContentPosition {
  TOP,
  CENTER;

  companion object {
    operator fun invoke(heightSizeClass: WindowHeightSizeClass): NavigationContentPosition =
      when (heightSizeClass) {
        WindowHeightSizeClass.Medium,
        WindowHeightSizeClass.Expanded -> {
          CENTER
        }
        else -> {
          TOP
        }
      }
  }
}
