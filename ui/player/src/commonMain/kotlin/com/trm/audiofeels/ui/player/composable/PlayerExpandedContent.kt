package com.trm.audiofeels.ui.player.composable

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.trm.audiofeels.ui.player.PlayerViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerExpandedContent(viewState: PlayerViewState, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      when (viewState) {
        is PlayerViewState.Invisible,
        is PlayerViewState.Error,
        is PlayerViewState.Loading -> {
          CircularProgressIndicator()
        }
        is PlayerViewState.Playback -> {
          HorizontalMultiBrowseCarousel(
            state = rememberCarouselState(viewState.currentTrackIndex) { viewState.tracks.size },
            preferredItemWidth = maxWidth / 2,
            contentPadding = PaddingValues(8.dp),
          ) {
            AsyncImage(
              model = viewState.tracks.getOrNull(it)?.artworkUrl,
              contentDescription = null,
              contentScale = ContentScale.FillBounds,
              modifier = Modifier.maskClip(MaterialTheme.shapes.extraLarge),
            )
          }
        }
      }
    }

    LazyColumn {
      when (viewState) {
        is PlayerViewState.Invisible,
        is PlayerViewState.Error,
        is PlayerViewState.Loading -> {}
        is PlayerViewState.Playback -> {
          items(viewState.tracks) { Text(it.title) }
        }
      }
    }
  }
}
