package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun calculateWindowSize(): DpSize =
  with(LocalDensity.current) { LocalWindowInfo.current.containerSize.toSize().toDpSize() }
