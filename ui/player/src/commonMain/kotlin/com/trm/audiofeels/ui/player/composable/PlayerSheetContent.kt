package com.trm.audiofeels.ui.player.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.trm.audiofeels.ui.player.PlayerViewState

@Composable
fun PlayerSheetContent(
  viewState: PlayerViewState,
  partiallyExpandedAlpha: Float,
  expandedAlpha: Float,
  showToggleFavourite: Boolean,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    if (partiallyExpandedAlpha > 0f) {
      PlayerCollapsedContent(
        viewState = viewState,
        modifier = Modifier.fillMaxWidth().alpha(partiallyExpandedAlpha),
      )
    }

    if (expandedAlpha > 0f) {
      PlayerExpandedContent(
        viewState = viewState,
        showToggleFavourite = showToggleFavourite,
        showEdgeGradients = false,
        modifier = Modifier.fillMaxSize().alpha(expandedAlpha),
      )
    }
  }
}
