package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.trm.audiofeels.core.ui.compose.AsyncShimmerImage
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.ui.player.PlayerViewState
import kotlin.math.absoluteValue

@Composable
fun PlayerExpandedContent(viewState: PlayerViewState, modifier: Modifier = Modifier) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Crossfade(viewState) {
      when (viewState) {
        is PlayerViewState.Invisible,
        is PlayerViewState.Error, // TODO: retry
        is PlayerViewState.Loading -> {
          CircularProgressIndicator()
        }
        is PlayerViewState.Playback -> {
          val pagerState =
            rememberPagerState(
              initialPage = viewState.currentTrackIndex,
              pageCount = viewState.tracks::size,
            )
          BoxWithConstraints(modifier = Modifier.weight(1f)) {
            HorizontalPager(
              state = pagerState,
              contentPadding = PaddingValues(horizontal = 64.dp, vertical = 16.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              val pageOffset = (pagerState.currentPage - it) + pagerState.currentPageOffsetFraction
              AsyncShimmerImage(
                model = viewState.tracks.getOrNull(it)?.artworkUrl,
                contentDescription = null,
                modifier = { enabled ->
                  Modifier.aspectRatio(1f)
                    .graphicsLayer {
                      val fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                      val scale = lerp(start = 0.85.dp, stop = 1.dp, fraction = fraction)
                      scaleX = scale.value
                      scaleY = scale.value
                      alpha = lerp(start = 0.5.dp, stop = 1.dp, fraction = fraction).value
                    }
                    .clip(MaterialTheme.shapes.extraLarge)
                    .shimmerBackground(enabled = enabled, shape = MaterialTheme.shapes.extraLarge)
                },
              )
            }
          }
        }
      }
    }
  }
}
