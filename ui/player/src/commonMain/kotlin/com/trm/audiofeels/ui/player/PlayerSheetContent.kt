package com.trm.audiofeels.ui.player

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

@Composable
fun PlayerSheetContent(viewState: PlayerViewState) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
  ) {
    // TODO: shimmer loading/error placeholder
    viewState.currentTrackImageBitmap?.let {
      Image(
        bitmap = it,
        contentDescription = null,
        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
      )
    }

    Column(
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(horizontal = 16.dp),
    ) {
      when (viewState) {
        is PlayerViewState.Invisible,
        is PlayerViewState.Error -> {}
        is PlayerViewState.Loading -> {
          PlaylistNameText(viewState.playlist)
        }
        is PlayerViewState.Playback -> {
          viewState.currentTrack?.let { TrackTitleText(it) }
          PlaylistNameText(viewState.playlist)
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

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

@Composable
private fun TrackTitleText(it: Track) {
  Text(
    text = it.title,
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
