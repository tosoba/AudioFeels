package com.trm.audiofeels.ui.player

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.domain.model.PlayerState

@Composable
fun PlayerSheetContent(viewState: PlayerViewState) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceAround,
    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
  ) {
    viewState.currentTrackImageBitmap?.let {
      Image(bitmap = it, contentDescription = null, modifier = Modifier.size(60.dp))
    }

    if (viewState is PlayerViewState.Playback) {
      Text(
        when (val playerState = viewState.playerState) {
          PlayerState.Idle -> {
            "Idle"
          }
          is PlayerState.Enqueued -> {
            "Enq ${playerState.currentTrackIndex} - ${viewState.currentTrackProgress}"
          }
          is PlayerState.Error -> {
            "Error"
          }
        }
      )
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
            IconButton(onClick = viewState.controlActions::onPreviousClick) {
              Icon(imageVector = Icons.Outlined.SkipPrevious, contentDescription = "Previous")
            }

            IconButton(onClick = viewState.controlActions::onTogglePlayClick) {
              // TODO: only show play/pause on IDLE/READY/maybe ENDED playback state
              // and show loading indicator on BUFFERING
              Crossfade(playerState.isPlaying) {
                if (it) Icon(imageVector = Icons.Outlined.Pause, contentDescription = "Pause")
                else Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = "Play")
              }
            }

            IconButton(onClick = viewState.controlActions::onNextClick) {
              Icon(imageVector = Icons.Outlined.SkipNext, contentDescription = "Next")
            }
          }
          is PlayerState.Error -> {
            // TODO: retry
          }
        }
      }

      is PlayerViewState.Error -> {
        Button(
          onClick = {
            // TODO: retry
          }
        ) {
          Text("Retry")
        }
      }
    }

    Button(onClick = viewState.playbackActions::cancel) { Text("Cancel") }
  }
}
