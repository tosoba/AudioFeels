package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect

fun Modifier.defaultHazeEffect(hazeState: HazeState, hazeStyle: HazeStyle): Modifier =
  hazeEffect(hazeState) {
    style = hazeStyle
    blurRadius = 10.dp
  }
