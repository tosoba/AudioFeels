package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal

interface DisplayManager {
  @Composable fun isNormalDevicePosture(): Boolean

  @Composable fun windowSizeClass(): WindowSizeClass
}

expect class DisplayPlatformManager : DisplayManager

@Composable
expect fun rememberDisplayManager(): DisplayManager
