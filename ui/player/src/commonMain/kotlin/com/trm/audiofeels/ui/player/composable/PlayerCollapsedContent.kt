package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
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
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.compose.AsyncShimmerImage
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.artwork_placeholder
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.ui.player.PlayerViewState
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlayerCollapsedContent(viewState: PlayerViewState, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    SwipeToDismissBox(
      state = rememberPlayerTrackActionsSwipeState(viewState),
      gesturesEnabled = viewState is PlayerViewState.Playback,
      enableDismissFromStartToEnd =
        viewState is PlayerViewState.Playback && viewState.canPlayPrevious,
      enableDismissFromEndToStart = viewState is PlayerViewState.Playback && viewState.canPlayNext,
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
            Box(
              modifier =
                Modifier.size(60.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .shimmerBackground(enabled = true, shape = RoundedCornerShape(12.dp))
            )
          }
          is PlayerViewState.Error -> {
            Image(
              painter = rememberVectorPainter(vectorResource(Res.drawable.artwork_placeholder)),
              contentDescription = null,
            )
          }
          is PlayerViewState.Playback -> {
            AsyncShimmerImage(
              model = viewState.currentTrack?.artworkUrl,
              contentDescription = null,
              modifier = { enabled ->
                Modifier.size(60.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .shimmerBackground(enabled = enabled, shape = RoundedCornerShape(12.dp))
              },
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

        PrimaryControl(controlState = viewState.primaryControlState)
      }
    }

    TrackProgressIndicator(
      visible =
        viewState is PlayerViewState.Playback && viewState.playerState is PlayerState.Enqueued
    ) {
      (viewState as? PlayerViewState.Playback)?.currentTrackProgress?.toFloat() ?: 0.0f
    }
  }
}

@Composable
private fun rememberPlayerTrackActionsSwipeState(
  viewState: PlayerViewState
): SwipeToDismissBoxState {
  val density = LocalDensity.current
  val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
  val confirmValueChange = { value: SwipeToDismissBoxValue ->
    if (viewState is PlayerViewState.Playback) {
      when (value) {
        SwipeToDismissBoxValue.StartToEnd -> viewState.trackActions.playPrevious()
        SwipeToDismissBoxValue.EndToStart -> viewState.trackActions.playNext()
        SwipeToDismissBoxValue.Settled -> {}
      }
    }
    false
  }
  return rememberSaveable(
    viewState,
    saver =
      SwipeToDismissBoxState.Saver(
        confirmValueChange = confirmValueChange,
        density = density,
        positionalThreshold = positionalThreshold,
      ),
  ) {
    SwipeToDismissBoxState(
      initialValue = SwipeToDismissBoxValue.Settled,
      density = density,
      confirmValueChange = confirmValueChange,
      positionalThreshold = positionalThreshold,
    )
  }
}

@Composable
private fun PrimaryControl(controlState: PlayerViewState.PrimaryControlState) {
  Crossfade(controlState) {
    when (it) {
      PlayerViewState.PrimaryControlState.Loading -> {
        CircularProgressIndicator()
      }
      is PlayerViewState.PrimaryControlState.Action -> {
        IconButton(onClick = it.action) {
          Icon(imageVector = it.imageVector, contentDescription = it.contentDescription)
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
