package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode

fun Modifier.shimmerBackground(enabled: Boolean, shape: Shape = RectangleShape): Modifier =
  composed {
    if (!enabled) return@composed this

    val translateAnimation by
      rememberInfiniteTransition()
        .animateFloat(
          initialValue = 0f,
          targetValue = 400f,
          animationSpec =
            infiniteRepeatable(
              tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
              RepeatMode.Restart,
            ),
        )
    return@composed this.then(
      background(
        brush =
          Brush.linearGradient(
            colors =
              listOf(
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
              ),
            start = Offset(translateAnimation, translateAnimation),
            end = Offset(translateAnimation + 100f, translateAnimation + 100f),
            tileMode = TileMode.Mirror,
          ),
        shape = shape,
      )
    )
  }
