package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun PlayerAudioVisualization(
  minValueColor: Color,
  maxValueColor: Color,
  values: List<Float>,
  animationDurationMs: Int,
  modifier: Modifier = Modifier,
  isPlaying: Boolean,
) {
  if (values.isEmpty()) return

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

  Canvas(modifier = modifier) {
    drawVisualization(
      animatedValues = animatedValues,
      minValueColor = minValueColor,
      maxValueColor = maxValueColor,
    )
  }
}

private fun DrawScope.drawVisualization(
  animatedValues: List<State<Float>>,
  minValueColor: Color,
  maxValueColor: Color,
) {
  val baseStrokeWidth = 2.dp.toPx()
  val maxStrokeWidth = 25.dp.toPx()

  val segmentsPerSide = 200
  val valuesPerSide = (animatedValues.size / 4).coerceAtLeast(1)

  fun getAudioValue(sideIndex: Int, progress: Float): Float {
    val valueIndex =
      (sideIndex * valuesPerSide + progress * valuesPerSide)
        .toInt()
        .coerceIn(0, animatedValues.size - 1)
    return animatedValues[valueIndex].value
  }

  fun getStrokeWidth(progress: Float, audioValue: Float): Float {
    val edgeDampening =
      when {
        progress < 0.25f -> progress / 0.25f
        progress > 0.75f -> (1f - progress) / 0.25f
        else -> 1f
      }

    val sineWave = sin(progress * PI).toFloat()
    val effectiveWave = sineWave * edgeDampening
    return baseStrokeWidth + (effectiveWave * audioValue * (maxStrokeWidth - baseStrokeWidth))
  }

  val layers =
    listOf(
      1.0f to 0.05f,
      0.9f to 0.12f,
      0.8f to 0.20f,
      0.7f to 0.30f,
      0.6f to 0.42f,
      0.5f to 0.55f,
      0.4f to 0.68f,
      0.3f to 0.80f,
      0.2f to 0.90f,
      0.1f to 1.0f,
    )

  for (i in 0 until segmentsPerSide) {
    val progress1 = i.toFloat() / segmentsPerSide
    val progress2 = (i + 1).toFloat() / segmentsPerSide
    val progressMid = (progress1 + progress2) / 2f

    val y1 = size.height * progress1
    val y2 = size.height * progress2

    val audioValue = getAudioValue(1, progressMid)
    val strokeWidth = getStrokeWidth(progressMid, audioValue)
    val baseColor = lerp(minValueColor, maxValueColor, audioValue)

    layers.forEach { (widthFactor, alphaFactor) ->
      drawLine(
        color = baseColor.copy(alpha = baseColor.alpha * alphaFactor),
        start = Offset(size.width, y1),
        end = Offset(size.width, y2),
        strokeWidth = strokeWidth * widthFactor,
        cap = StrokeCap.Round,
      )
    }
  }

  for (i in 0 until segmentsPerSide) {
    val progress1 = i.toFloat() / segmentsPerSide
    val progress2 = (i + 1).toFloat() / segmentsPerSide
    val progressMid = (progress1 + progress2) / 2f

    val y1 = size.height * (1f - progress1)
    val y2 = size.height * (1f - progress2)

    val audioValue = getAudioValue(3, progressMid)
    val strokeWidth = getStrokeWidth(progressMid, audioValue)
    val baseColor = lerp(minValueColor, maxValueColor, audioValue)

    layers.forEach { (widthFactor, alphaFactor) ->
      drawLine(
        color = baseColor.copy(alpha = baseColor.alpha * alphaFactor),
        start = Offset(0f, y1),
        end = Offset(0f, y2),
        strokeWidth = strokeWidth * widthFactor,
        cap = StrokeCap.Round,
      )
    }
  }
}
