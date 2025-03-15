package com.trm.audiofeels.ui.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

@Composable
fun PlayerAudioVisualization(
  minValueColor: Color,
  maxValueColor: Color,
  values: List<Float>,
  animationDurationMs: Int,
  modifier: Modifier = Modifier,
  isPlaying: Boolean,
) {
  val animatedValues =
    remember(values.size) { mutableStateListOf<State<Float>>() }
      .apply {
        repeat(values.size) { index ->
          val animate =
            animateFloatAsState(
              targetValue =
                if (isPlaying) values[index] else getOrNull(index)?.value ?: values[index],
              animationSpec = tween(durationMillis = animationDurationMs, easing = LinearEasing),
            )
          if (getOrNull(index) == null) {
            add(animate)
          } else {
            set(index, animate)
          }
        }
      }
  val strokeWidthDp by
    remember(values) {
      derivedStateOf {
        (animatedValues.sumOf { it.value.toDouble() }.toFloat() / animatedValues.size * 12).dp
      }
    }

  Canvas(modifier = modifier) {
    val strokeWidthPx = strokeWidthDp.toPx()
    val halfStrokeWidthPx = strokeWidthPx / 2f
    val topLeft = Offset(x = halfStrokeWidthPx, y = halfStrokeWidthPx)
    drawRect(
      brush =
        Brush.linearGradient(
          colors = animatedValues.map { lerp(minValueColor, maxValueColor, it.value) }
        ),
      topLeft = topLeft,
      size =
        Size(
          width = size.width - topLeft.x - halfStrokeWidthPx,
          height = size.height - topLeft.y - halfStrokeWidthPx,
        ),
      style = Stroke(strokeWidthPx),
    )
  }
}
