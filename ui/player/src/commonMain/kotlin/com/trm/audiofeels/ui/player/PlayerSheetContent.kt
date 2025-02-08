package com.trm.audiofeels.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

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
      PlayerPartiallyExpandedSheetContent(
        viewState = viewState,
        modifier = Modifier.fillMaxWidth().alpha(partiallyExpandedAlpha),
      )
    }

    if (expandedAlpha > 0f) {
      PlayerExpandedSheetContent(
        viewState = viewState,
        modifier = Modifier.fillMaxSize().alpha(expandedAlpha),
      )
    }
  }
}

@Composable
private fun PlayerExpandedSheetContent(viewState: PlayerViewState, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
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

@Composable
private fun PlayerPartiallyExpandedSheetContent(
  viewState: PlayerViewState,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    TrackProgressIndicator(
      visible =
        viewState is PlayerViewState.Playback && viewState.playerState is PlayerState.Enqueued
    ) {
      (viewState as? PlayerViewState.Playback)?.currentTrackProgress?.toFloat() ?: 0.0f
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
        Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
    ) {
      when (viewState) {
        is PlayerViewState.Invisible,
        is PlayerViewState.Loading -> {
          // TODO: loading shimmer
        }
        is PlayerViewState.Error -> {
          // TODO: error image
        }
        is PlayerViewState.Playback -> {
          AsyncImage(
            model = viewState.currentTrack?.artworkUrl,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
          )
        }
      }

      Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
      ) {
        when (viewState) {
          is PlayerViewState.Invisible,
          is PlayerViewState.Error -> {
            // TODO: error text?
            Spacer(modifier = Modifier.weight(1f))
          }
          is PlayerViewState.Loading -> {
            PlaylistNameText(viewState.playlist)
          }
          is PlayerViewState.Playback -> {
            viewState.currentTrack?.let { TrackTitleText(it) }
            PlaylistNameText(viewState.playlist)
          }
        }
      }

      when (viewState) {
        is PlayerViewState.Invisible,
        is PlayerViewState.Loading -> {
          CircularProgressIndicator()
        }
        is PlayerViewState.Playback -> {
          when (val playerState = viewState.playerState) {
            PlayerState.Idle -> {
              IconButton(onClick = viewState.controlActions::onTogglePlayClick) {
                Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Play")
              }
            }
            is PlayerState.Enqueued -> {
              IconButton(onClick = viewState.controlActions::onTogglePlayClick) {
                // TODO: only show play/pause on IDLE/READY/maybe ENDED playback state
                // and show loading indicator on BUFFERING
                Crossfade(playerState.isPlaying) {
                  if (it) Icon(imageVector = Icons.Outlined.Pause, contentDescription = "Pause")
                  else Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Play")
                }
              }
            }
            is PlayerState.Error -> {
              IconButton(
                onClick = {
                  // TODO: retry action depending on player error type in VM
                }
              ) {
                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Retry")
              }
            }
          }
        }
        is PlayerViewState.Error -> {
          IconButton(
            onClick = {
              // TODO: retry action depending on player error type in VM
            }
          ) {
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Retry")
          }
        }
      }
    }
  }
}

@Composable
private fun BoxScope.TrackProgressIndicator(visible: Boolean, progress: () -> Float) {
  AnimatedVisibility(
    visible = visible,
    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
  ) {
    LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
  }
}

@Composable
private fun TrackTitleText(track: Track) {
  Text(
    text = track.title,
    style = MaterialTheme.typography.labelLarge,
    modifier = Modifier.basicMarquee(),
  )
}

@Composable
private fun PlaylistNameText(playlist: Playlist) {
  Text(
    text = playlist.name,
    style = MaterialTheme.typography.labelMedium,
    modifier = Modifier.basicMarquee(),
  )
}
