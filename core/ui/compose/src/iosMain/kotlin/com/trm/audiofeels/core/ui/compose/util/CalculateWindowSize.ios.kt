package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun calculateWindowSize(): DpSize =
  LocalWindowInfo.current.containerSize.toSize().toLocalDensityDpSize()
