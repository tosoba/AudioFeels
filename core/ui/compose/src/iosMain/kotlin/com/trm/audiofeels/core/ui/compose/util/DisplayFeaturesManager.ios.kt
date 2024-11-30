package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.runtime.Composable

actual class DisplayFeaturesPlatformManager : DisplayFeaturesManager {
  @Composable override fun isNormalDevicePosture(): Boolean = true
}
