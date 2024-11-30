package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

actual class DisplayPlatformManager : DisplayManager {
  @Composable override fun isNormalDevicePosture(): Boolean = true

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Composable
  override fun windowSizeClass(): WindowSizeClass = calculateWindowSizeClass()
}
