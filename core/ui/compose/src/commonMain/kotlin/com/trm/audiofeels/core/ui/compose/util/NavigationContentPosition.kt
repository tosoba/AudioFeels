package com.trm.audiofeels.core.ui.compose.util

import androidx.window.core.layout.WindowHeightSizeClass

enum class NavigationContentPosition {
  TOP,
  CENTER;

  companion object {
    operator fun invoke(heightSizeClass: WindowHeightSizeClass): NavigationContentPosition =
      when (heightSizeClass) {
        WindowHeightSizeClass.MEDIUM,
        WindowHeightSizeClass.EXPANDED -> {
          CENTER
        }
        else -> {
          TOP
        }
      }
  }
}
