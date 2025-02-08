package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.ui.player.PlayerViewState
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlayerCollapsedContent(viewState: PlayerViewState, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    LaunchedEffect(viewState) {
      Napier.e(tag = "PV", message = viewState::class.simpleName.toString())
    }

    TrackProgressIndicator(
      visible =
        viewState is PlayerViewState.Playback && viewState.playerState is PlayerState.Enqueued
    ) {
      (viewState as? PlayerViewState.Playback)?.currentTrackProgress?.toFloat() ?: 0.0f
    }

    val playPrevious =
      remember(viewState) {
        { (viewState as? PlayerViewState.Playback)?.controlActions?.onPlayPrevious() }
      }
    val playNext =
      remember(viewState) {
        { (viewState as? PlayerViewState.Playback)?.controlActions?.onPlayNext() }
      }

    val swipeBoxState =
      rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
          when (value) {
            SwipeToDismissBoxValue.StartToEnd -> playPrevious()
            SwipeToDismissBoxValue.EndToStart -> playNext()
            SwipeToDismissBoxValue.Settled -> {}
          }
          false
        }
      )
    SwipeToDismissBox(
      state = swipeBoxState,
      gesturesEnabled = viewState is PlayerViewState.Playback,
      backgroundContent = {
        Row(
          modifier =
            Modifier.fillMaxSize()
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Outlined.SkipPrevious,
            contentDescription = "Play previous track",
          )
          Spacer(modifier = Modifier.weight(1f))
          Icon(imageVector = Icons.Outlined.SkipNext, contentDescription = "Play next track")
        }
      },
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
          Modifier.fillMaxWidth()
            .background(BottomSheetDefaults.ContainerColor)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
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
                IconButton(onClick = viewState.controlActions::onTogglePlay) {
                  Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Play")
                }
              }
              is PlayerState.Enqueued -> {
                IconButton(onClick = viewState.controlActions::onTogglePlay) {
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
