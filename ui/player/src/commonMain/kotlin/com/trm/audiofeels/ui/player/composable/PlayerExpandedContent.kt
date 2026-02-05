package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.min
import androidx.compose.ui.util.lerp
import com.trm.audiofeels.core.ui.compose.AsyncShimmerImage
import com.trm.audiofeels.core.ui.compose.EndEdgeGradient
import com.trm.audiofeels.core.ui.compose.StartEdgeGradient
import com.trm.audiofeels.core.ui.compose.theme.Spacing
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.artwork_placeholder
import com.trm.audiofeels.core.ui.resources.error_occurred
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.ui.player.PlayerViewState
import com.trm.audiofeels.ui.player.util.currentTrackProgressOrZero
import kotlin.math.absoluteValue
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PlayerExpandedContent(
  viewState: PlayerViewState,
  currentPlaylist: Playlist?,
  showSlider: Boolean,
  showEdgeGradients: Boolean,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(modifier = modifier) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
      val pagerModifier =
        Modifier.fillMaxWidth()
          .heightIn(
            max = min(this@BoxWithConstraints.maxWidth, this@BoxWithConstraints.maxHeight * .6f)
          )
      when (viewState) {
        is PlayerViewState.Invisible -> {}
        is PlayerViewState.Loading -> {
          PlayerTrackPlaceholdersPager(modifier = pagerModifier)
        }
        is PlayerViewState.Playback -> {
          PlayerTracksPager(viewState = viewState, modifier = pagerModifier)
        }
        is PlayerViewState.Error -> {
          PlayerErrorPager(modifier = pagerModifier)
        }
      }

      if (showSlider) {
        AnimatedVisibility(visible = viewState is PlayerViewState.Playback) {
          var sliderValue by
            remember(viewState) { mutableFloatStateOf(viewState.currentTrackProgressOrZero) }
          Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {
              (viewState as? PlayerViewState.Playback)?.seekToProgress?.invoke(sliderValue)
            },
            enabled = viewState is PlayerViewState.Playback,
            modifier = Modifier.fillMaxWidth().padding(Spacing.medium16dp),
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth().weight(1f),
      ) {
        PlaylistFavouriteToggleButton(
          checked = currentPlaylist?.favourite == true,
          enabled = viewState is PlayerViewState.Playback,
          onCheckedChange = {
            (viewState as? PlayerViewState.Playback)?.togglePlaylistFavourite?.invoke()
          },
        )

        PlayerPrimaryControl(viewState.primaryControlState)

        PlayerCancelPlaybackButton(onClick = viewState.cancelPlayback)
      }
    }

    if (showEdgeGradients) {
      StartEdgeGradient()
      EndEdgeGradient()
    }
  }
}

@Composable
private fun PlayerTrackPlaceholdersPager(modifier: Modifier = Modifier) {
  val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
  HorizontalPager(
    state = pagerState,
    contentPadding = playerTracksPagerContentPadding,
    userScrollEnabled = false,
    modifier = modifier,
  ) {
    Column(
      modifier =
        Modifier.clip(MaterialTheme.shapes.extraLarge)
          .scale(if (it == pagerState.currentPage) 1f else playerTracksPagerItemMinScale)
          .shimmerBackground(enabled = true, shape = MaterialTheme.shapes.extraLarge)
          .alpha(if (it == pagerState.currentPage) 1f else playerTracksPagerItemMinAlpha)
    ) {
      Box(modifier = Modifier.fillMaxWidth().weight(1f))
      Text(
        text = "",
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.fillMaxWidth().padding(Spacing.mediumSmall12dp),
      )
    }
  }
}

@Composable
private fun PlayerErrorPager(modifier: Modifier = Modifier) {
  HorizontalPager(
    state = rememberPagerState(initialPage = 1, pageCount = { 1 }),
    contentPadding = playerTracksPagerContentPadding,
    userScrollEnabled = false,
    modifier = modifier,
  ) {
    Card(shape = MaterialTheme.shapes.extraLarge) {
      Image(
        painter = rememberVectorPainter(vectorResource(Res.drawable.artwork_placeholder)),
        contentDescription = null,
        modifier =
          Modifier.fillMaxWidth().weight(1f).background(MaterialTheme.colorScheme.errorContainer),
      )
      Text(
        text = stringResource(Res.string.error_occurred),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(Spacing.mediumSmall12dp),
      )
    }
  }
}

@Composable
private fun PlayerTracksPager(viewState: PlayerViewState.Playback, modifier: Modifier = Modifier) {
  val pagerState =
    rememberPagerState(
      initialPage = viewState.currentTrackIndex,
      pageCount = viewState.tracks::size,
    )
  LaunchedEffect(pagerState.settledPage) { viewState.playTrackAtIndex(pagerState.settledPage) }
  LaunchedEffect(viewState.currentTrackIndex) {
    pagerState.animateScrollToPage(viewState.currentTrackIndex)
  }

  HorizontalPager(
    state = pagerState,
    contentPadding = playerTracksPagerContentPadding,
    modifier = modifier,
  ) {
    val track = viewState.tracks.getOrNull(it) ?: return@HorizontalPager
    val pageOffset = pagerState.currentPage - it + pagerState.currentPageOffsetFraction

    Card(
      modifier =
        Modifier.graphicsLayer {
          val fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
          val scale = lerp(start = playerTracksPagerItemMinScale, stop = 1f, fraction = fraction)
          scaleX = scale
          scaleY = scale
          alpha = lerp(start = playerTracksPagerItemMinAlpha, stop = 1f, fraction = fraction)
        },
      shape = MaterialTheme.shapes.extraLarge,
    ) {
      AsyncShimmerImage(
        model = track.artworkUrl,
        contentDescription = null,
        modifier = { enabled ->
          Modifier.fillMaxWidth().weight(1f).shimmerBackground(enabled = enabled)
        },
      )
      Text(
        text = track.title,
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth().padding(Spacing.small8dp).basicMarquee(),
      )
    }
  }
}

private val playerTracksPagerContentPadding =
  PaddingValues(horizontal = Spacing.extraLarge64dp, vertical = Spacing.medium16dp)
private const val playerTracksPagerItemMinScale = .85f
private const val playerTracksPagerItemMinAlpha = .5f
