package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize

@Composable
fun Size.toLocalDensityDpSize(): DpSize =
  with(LocalDensity.current) { this@toLocalDensityDpSize.toDpSize() }
