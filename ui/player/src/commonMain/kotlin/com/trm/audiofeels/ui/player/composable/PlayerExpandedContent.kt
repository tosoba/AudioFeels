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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
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
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerExpandedContent(
  viewState: PlayerViewState,
  currentPlaylist: Playlist?,
  showEdgeGradients: Boolean,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(modifier = modifier) {
    val expandedCutoff = 600.dp
    val isExpanded = this@BoxWithConstraints.maxWidth >= expandedCutoff

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
      val pagerModifier =
        Modifier.fillMaxWidth()
          .heightIn(
            max = min(this@BoxWithConstraints.maxWidth, this@BoxWithConstraints.maxHeight * .5f)
          )
      when (viewState) {
        is PlayerViewState.Invisible -> {}
        is PlayerViewState.Loading -> {
          PlayerTrackPlaceholdersPager(isExpanded = isExpanded, modifier = pagerModifier)
        }
        is PlayerViewState.Playback -> {
          PlayerTracksPager(
            viewState = viewState,
            isExpanded = isExpanded,
            modifier = pagerModifier,
          )
        }
        is PlayerViewState.Error -> {
          PlayerErrorPager(isExpanded = isExpanded, modifier = pagerModifier)
        }
      }

      if (this@BoxWithConstraints.maxHeight > 480.dp) {
        Spacer(modifier = Modifier.height(Spacing.medium16dp))

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
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium16dp),
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth().padding(Spacing.medium16dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerTrackPlaceholdersPager(isExpanded: Boolean, modifier: Modifier = Modifier) {
  val carouselState = rememberCarouselState(initialItem = 1) { 3 }
  val (minSmallItemWidth, maxSmallItemWidth) = getSmallItemWidths(isExpanded)

  HorizontalCenteredHeroCarousel(
    state = carouselState,
    itemSpacing = Spacing.medium16dp,
    minSmallItemWidth = minSmallItemWidth,
    maxSmallItemWidth = maxSmallItemWidth,
    userScrollEnabled = false,
    contentPadding = PaddingValues(horizontal = Spacing.medium16dp),
    modifier = modifier,
  ) {
    Column(
      modifier =
        Modifier.maskClip(MaterialTheme.shapes.extraLarge)
          .shimmerBackground(enabled = true, shape = MaterialTheme.shapes.extraLarge)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerErrorPager(isExpanded: Boolean, modifier: Modifier = Modifier) {
  val carouselState = rememberCarouselState(initialItem = 0) { 1 }
  val (minSmallItemWidth, maxSmallItemWidth) = getSmallItemWidths(isExpanded)

  HorizontalCenteredHeroCarousel(
    state = carouselState,
    itemSpacing = Spacing.medium16dp,
    minSmallItemWidth = minSmallItemWidth,
    maxSmallItemWidth = maxSmallItemWidth,
    userScrollEnabled = false,
    contentPadding = PaddingValues(horizontal = Spacing.medium16dp),
    modifier = modifier,
  ) {
    Card(
      shape = MaterialTheme.shapes.extraLarge,
      modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge),
    ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerTracksPager(
  viewState: PlayerViewState.Playback,
  isExpanded: Boolean,
  modifier: Modifier = Modifier,
) {
  val carouselState =
    rememberCarouselState(
      initialItem = viewState.currentTrackIndex,
      itemCount = { viewState.tracks.size },
    )
  val (minSmallItemWidth, maxSmallItemWidth) = getSmallItemWidths(isExpanded)

  LaunchedEffect(carouselState.currentItem) {
    viewState.playTrackAtIndex(carouselState.currentItem)
  }

  LaunchedEffect(viewState.currentTrackIndex) {
    carouselState.scrollToItem(viewState.currentTrackIndex)
  }

  HorizontalCenteredHeroCarousel(
    state = carouselState,
    itemSpacing = Spacing.medium16dp,
    minSmallItemWidth = minSmallItemWidth,
    maxSmallItemWidth = maxSmallItemWidth,
    contentPadding = PaddingValues(horizontal = Spacing.medium16dp),
    modifier = modifier,
  ) { index ->
    val track = viewState.tracks.getOrNull(index) ?: return@HorizontalCenteredHeroCarousel

    Card(
      modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge),
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

@OptIn(ExperimentalMaterial3Api::class)
private fun getSmallItemWidths(isExpanded: Boolean): Pair<Dp, Dp> {
  val multiplier = if (isExpanded) 3f else 1f
  return Pair(
    CarouselDefaults.MinSmallItemSize * multiplier,
    CarouselDefaults.MaxSmallItemSize * multiplier,
  )
}
