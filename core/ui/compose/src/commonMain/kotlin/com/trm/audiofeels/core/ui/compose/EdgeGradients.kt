package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.compose.theme.GRADIENT_BASE_ALPHA

@Composable
fun BoxScope.StartEdgeGradient() {
  Box(
    modifier =
      Modifier.fillMaxHeight()
        .width(GRADIENT_SIZE)
        .align(Alignment.TopStart)
        .background(Brush.horizontalGradient(fullyOpaqueToTransparentColors()))
  )
}

@Composable
fun BoxScope.EndEdgeGradient() {
  Box(
    modifier =
      Modifier.fillMaxHeight()
        .width(GRADIENT_SIZE)
        .align(Alignment.TopEnd)
        .background(Brush.horizontalGradient(transparentToFullyOpaqueColors()))
  )
}

@Composable
fun BoxScope.BottomEdgeGradient() {
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .height(GRADIENT_SIZE)
        .align(Alignment.BottomStart)
        .background(Brush.verticalGradient(transparentToFullyOpaqueColors()))
  )
}

@Composable
fun TopEdgeGradient(topOffset: Dp) {
  Column(modifier = Modifier.fillMaxWidth().height(topOffset + 8.dp)) {
    Spacer(modifier = Modifier.weight(1f))
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .height(GRADIENT_SIZE)
          .background(
            Brush.verticalGradient(
              listOf(
                MaterialTheme.colorScheme.background.copy(alpha = GRADIENT_BASE_ALPHA),
                MaterialTheme.colorScheme.background.copy(alpha = 0f),
              )
            )
          )
    )
  }
}

@Composable
private fun fullyOpaqueToTransparentColors(): List<Color> =
  listOf(
    MaterialTheme.colorScheme.background,
    MaterialTheme.colorScheme.background.copy(alpha = 0f),
  )

@Composable
private fun transparentToFullyOpaqueColors(): List<Color> =
  listOf(
    MaterialTheme.colorScheme.background.copy(alpha = 0f),
    MaterialTheme.colorScheme.background,
  )

private val GRADIENT_SIZE = 8.dp
