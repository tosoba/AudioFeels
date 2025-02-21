package com.trm.audiofeels.ui.player.composable

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
    when (viewState) {
      is PlayerViewState.Invisible -> {}
      is PlayerViewState.Loading -> PlayerTrackPlaceholdersPager()
      is PlayerViewState.Playback -> PlayerTracksPager(viewState)
      is PlayerViewState.Error -> {}
    }

    // TODO: retry control on playback error

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

@Composable
private fun PlayerTrackPlaceholdersPager() {
  val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
  HorizontalPager(
    state = pagerState,
    contentPadding = PaddingValues(horizontal = 64.dp, vertical = 16.dp),
    userScrollEnabled = false,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier =
        Modifier.clip(MaterialTheme.shapes.extraLarge)
          .scale(if (it == pagerState.currentPage) 1f else .85f)
          .shimmerBackground(enabled = true, shape = MaterialTheme.shapes.extraLarge)
          .alpha(if (it == pagerState.currentPage) 1f else .5f)
    ) {
      Box(modifier = Modifier.aspectRatio(1f))
      Text(
        text = "",
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.fillMaxWidth().padding(12.dp),
      )
    }
  }
}

@Composable
private fun PlayerTracksPager(viewState: PlayerViewState.Playback) {
  val pagerState =
    rememberPagerState(
      initialPage = viewState.currentTrackIndex,
      pageCount = viewState.tracks::size,
    )
  LaunchedEffect(pagerState.settledPage) {
    viewState.trackActions.playAtIndex(pagerState.settledPage)
  }

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
        modifier = { enabled -> Modifier.aspectRatio(1f).shimmerBackground(enabled = enabled) },
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
