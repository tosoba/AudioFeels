package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.trm.audiofeels.core.ui.compose.AsyncShimmerImage
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.ui.player.PlayerViewState
import kotlin.math.absoluteValue

@Composable
fun PlayerExpandedContent(viewState: PlayerViewState, modifier: Modifier = Modifier) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    Crossfade(viewState::class) {
      when (viewState) {
        is PlayerViewState.Invisible,
        is PlayerViewState.Loading -> {
          CircularProgressIndicator() // TODO: use HorizontalPager with shimmer items for loading
        }
        is PlayerViewState.Playback -> {
          val pagerState =
            rememberPagerState(
              initialPage = viewState.currentTrackIndex,
              pageCount = viewState.tracks::size,
            )
          LaunchedEffect(pagerState.settledPage) {
            viewState.trackActions.playAtIndex(pagerState.settledPage)
          }

          BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
              state = pagerState,
              contentPadding = PaddingValues(horizontal = 64.dp, vertical = 16.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              val track = viewState.tracks.getOrNull(it) ?: return@HorizontalPager
              val pageOffset = (pagerState.currentPage - it) + pagerState.currentPageOffsetFraction

              Card(
                modifier =
                  Modifier.graphicsLayer {
                    val fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                    val scale = lerp(start = 0.85.dp, stop = 1.dp, fraction = fraction)
                    scaleX = scale.value
                    scaleY = scale.value
                    alpha = lerp(start = 0.5.dp, stop = 1.dp, fraction = fraction).value
                  },
                shape = MaterialTheme.shapes.extraLarge,
              ) {
                AsyncShimmerImage(
                  model = track.artworkUrl,
                  contentDescription = null,
                  modifier = { enabled ->
                    Modifier.aspectRatio(1f).shimmerBackground(enabled = enabled)
                  },
                )
                Text(
                  text = track.title,
                  style = MaterialTheme.typography.labelLarge,
                  textAlign = TextAlign.Center,
                  maxLines = 1,
                  modifier = Modifier.fillMaxWidth().padding(12.dp).basicMarquee(),
                )
              }
            }
          }
        }
        is PlayerViewState.Error -> {
          // TODO: retry
        }
      }
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier.fillMaxWidth().weight(1f),
    ) {
      IconButton(onClick = {}) {
        Icon(Icons.Outlined.Shuffle, contentDescription = "Shuffle remaining tracks")
      }

      PlayerPrimaryControl(viewState.primaryControlState)

      IconButton(onClick = {}) {
        Icon(Icons.Outlined.Favorite, contentDescription = "Toggle favourite")
      }
    }
  }
}
