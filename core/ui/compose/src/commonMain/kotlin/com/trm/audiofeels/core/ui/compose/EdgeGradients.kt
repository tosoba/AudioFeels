package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.StartEdgeGradient() {
  Box(
    modifier =
      Modifier.fillMaxHeight()
        .width(GRADIENT_SIZE)
        .align(Alignment.TopStart)
        .background(Brush.horizontalGradient(opaqueToTransparentColors()))
  )
}

@Composable
fun BoxScope.EndEdgeGradient() {
  Box(
    modifier =
      Modifier.fillMaxHeight()
        .width(GRADIENT_SIZE)
        .align(Alignment.TopEnd)
        .background(Brush.horizontalGradient(transparentToOpaqueColors()))
  )
}

@Composable
fun BoxScope.BottomEdgeGradient() {
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .height(GRADIENT_SIZE)
        .align(Alignment.BottomStart)
        .background(Brush.verticalGradient(transparentToOpaqueColors()))
  )
}

val GRADIENT_SIZE = 8.dp

@Composable
fun opaqueToTransparentColors(): List<Color> =
  listOf(
    MaterialTheme.colorScheme.background,
    MaterialTheme.colorScheme.background.copy(alpha = 0f),
  )

@Composable
fun transparentToOpaqueColors(): List<Color> =
  listOf(
    MaterialTheme.colorScheme.background.copy(alpha = 0f),
    MaterialTheme.colorScheme.background,
  )
