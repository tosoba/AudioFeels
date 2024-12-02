package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.toSize

@Composable
actual fun calculateWindowSize(): DpSize = currentWindowSize().toSize().toLocalDensityDpSize()
