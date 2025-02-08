package com.trm.audiofeels.ui.player.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.ui.player.PlayerViewState

@Composable
fun PlayerSheetContent(viewState: PlayerViewState, sheetOffset: Float) {
  val density = LocalDensity.current
  var sheetHeightPx by remember { mutableStateOf(0f) }

  val transitionProgress =
    remember(sheetOffset, sheetHeightPx) {
      if (sheetHeightPx > 0f) (sheetOffset / sheetHeightPx).coerceIn(0f, 1f) else 0f
    }

  val transitionThreshold = 0.5f
  val thresholdProgress =
    remember(transitionProgress) {
      ((transitionProgress - transitionThreshold) / (1f - transitionThreshold)).coerceIn(0f, 1f)
    }

  val expandedAlpha = remember(thresholdProgress) { 1f - thresholdProgress }.coerceIn(0f, 1f)
  val partiallyExpandedAlpha = remember(thresholdProgress) { thresholdProgress }.coerceIn(0f, 1f)

  Box(
    modifier =
      Modifier.fillMaxSize().onGloballyPositioned { layoutCoordinates ->
        sheetHeightPx = layoutCoordinates.size.height.toFloat() - with(density) { 128.dp.toPx() }
      }
  ) {
    if (partiallyExpandedAlpha > 0f) {
      PlayerCollapsedContent(
        viewState = viewState,
        modifier = Modifier.fillMaxWidth().alpha(partiallyExpandedAlpha),
      )
    }

    if (expandedAlpha > 0f) {
      PlayerExpandedContent(
        viewState = viewState,
        modifier = Modifier.fillMaxSize().alpha(expandedAlpha),
      )
    }
  }
}
