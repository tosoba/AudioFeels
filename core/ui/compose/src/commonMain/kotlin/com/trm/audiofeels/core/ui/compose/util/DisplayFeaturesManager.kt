package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.runtime.Composable

interface DisplayFeaturesManager {
  @Composable fun isNormalDevicePosture(): Boolean
}

expect class DisplayFeaturesPlatformManager : DisplayFeaturesManager
