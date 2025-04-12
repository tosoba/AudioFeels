package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
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
import com.trm.audiofeels.core.ui.compose.theme.RoundedCornerSize
import com.trm.audiofeels.core.ui.compose.theme.Spacing
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.artwork_placeholder
import com.trm.audiofeels.core.ui.resources.error_occurred
import com.trm.audiofeels.core.ui.resources.play_next_track
import com.trm.audiofeels.core.ui.resources.play_previous_track
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.ui.player.PlayerViewState
import com.trm.audiofeels.ui.player.util.currentTrackProgressOrZero
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun PlayerCollapsedContent(
  viewState: PlayerViewState,
  currentPlaylist: Playlist?,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    val playback = viewState as? PlayerViewState.Playback
    val swipeState =
      rememberPlayerSwipeState(
        onStartToEndSwipe = playback?.playPreviousTrack,
        onEndToStartSwipe = playback?.playNextTrack,
      )
    SwipeToDismissBox(
      state = swipeState,
      gesturesEnabled = viewState is PlayerViewState.Playback,
      enableDismissFromStartToEnd =
        viewState is PlayerViewState.Playback && viewState.canPlayPrevious,
      enableDismissFromEndToStart = viewState is PlayerViewState.Playback && viewState.canPlayNext,
      backgroundContent = {
        AnimatedVisibility(
          visible = viewState is PlayerViewState.Playback,
          enter = fadeIn(),
          exit = fadeOut(),
        ) {
          Row(
            modifier =
              Modifier.fillMaxSize()
                .padding(
                  start = Spacing.medium16dp,
                  end = Spacing.medium16dp,
                  top = Spacing.small8dp,
                  bottom = Spacing.mediumSmall12dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            AnimatedVisibility(
              visible = swipeState.targetValue == SwipeToDismissBoxValue.StartToEnd
            ) {
              Icon(
                imageVector = Icons.Outlined.SkipPrevious,
                contentDescription = stringResource(Res.string.play_previous_track),
              )
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
              visible = swipeState.targetValue == SwipeToDismissBoxValue.EndToStart
            ) {
              Icon(
                imageVector = Icons.Outlined.SkipNext,
                contentDescription = stringResource(Res.string.play_next_track),
              )
            }
          }
        }
      },
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
          Modifier.fillMaxWidth()
            .padding(
              start = Spacing.medium16dp,
              end = Spacing.medium16dp,
              top = Spacing.small8dp,
              bottom = Spacing.mediumSmall12dp,
            ),
      ) {
        val imageSize = 60.dp
        when (viewState) {
          is PlayerViewState.Invisible,
          is PlayerViewState.Loading -> {
            Box(
              modifier =
                Modifier.size(imageSize)
                  .clip(RoundedCornerShape(RoundedCornerSize.mediumSmall12dp))
                  .shimmerBackground(
                    enabled = true,
                    shape = RoundedCornerShape(RoundedCornerSize.mediumSmall12dp),
                  )
            )
          }
          is PlayerViewState.Error -> {
            Image(
              painter = rememberVectorPainter(vectorResource(Res.drawable.artwork_placeholder)),
              contentDescription = null,
              modifier =
                Modifier.size(imageSize)
                  .clip(RoundedCornerShape(RoundedCornerSize.mediumSmall12dp))
                  .background(MaterialTheme.colorScheme.errorContainer),
            )
          }
          is PlayerViewState.Playback -> {
            AsyncShimmerImage(
              model = viewState.currentTrack?.artworkUrl,
              contentDescription = null,
              modifier = { enabled ->
                Modifier.size(imageSize)
                  .clip(RoundedCornerShape(RoundedCornerSize.mediumSmall12dp))
                  .shimmerBackground(
                    enabled = enabled,
                    shape = RoundedCornerShape(RoundedCornerSize.mediumSmall12dp),
                  )
              },
            )
          }
        }

        Column(
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.weight(1f).padding(horizontal = Spacing.medium16dp),
        ) {
          when (viewState) {
            is PlayerViewState.Invisible -> {
              Spacer(modifier = Modifier.weight(1f))
            }
            is PlayerViewState.Loading -> {
              SecondaryText(text = currentPlaylist?.name.orEmpty())
            }
            is PlayerViewState.Playback -> {
              viewState.currentTrack?.let { PrimaryText(text = it.title) }
              SecondaryText(text = currentPlaylist?.name.orEmpty())
            }
            is PlayerViewState.Error -> {
              PrimaryText(text = stringResource(Res.string.error_occurred))
            }
          }
        }

        PlaylistFavouriteToggleButton(
          checked = currentPlaylist?.favourite == true,
          enabled = viewState is PlayerViewState.Playback,
          onCheckedChange = {
            (viewState as? PlayerViewState.Playback)?.togglePlaylistFavourite?.invoke()
          },
        )

        Spacer(modifier = Modifier.width(Spacing.small8dp))

        PlayerPrimaryControl(controlState = viewState.primaryControlState)
      }
    }

    TrackProgressIndicator(
      visible = viewState is PlayerViewState.Playback,
      progress = viewState::currentTrackProgressOrZero,
    )
  }
}

@Composable
private fun rememberPlayerSwipeState(
  onStartToEndSwipe: (() -> Unit)?,
  onEndToStartSwipe: (() -> Unit)?,
): SwipeToDismissBoxState {
  val density = LocalDensity.current
  val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
  val confirmValueChange = { value: SwipeToDismissBoxValue ->
    if (onStartToEndSwipe != null && onEndToStartSwipe != null) {
      when (value) {
        SwipeToDismissBoxValue.StartToEnd -> onStartToEndSwipe()
        SwipeToDismissBoxValue.EndToStart -> onEndToStartSwipe()
        SwipeToDismissBoxValue.Settled -> {}
      }
    }
    false
  }
  return rememberSaveable(
    onStartToEndSwipe,
    onEndToStartSwipe,
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
private fun BoxScope.TrackProgressIndicator(visible: Boolean, progress: () -> Float) {
  AnimatedVisibility(
    visible = visible,
    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
  ) {
    LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
  }
}

@Composable
private fun PrimaryText(text: String) {
  Text(text = text, style = MaterialTheme.typography.labelLarge, modifier = Modifier.basicMarquee())
}

@Composable
private fun SecondaryText(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelMedium,
    modifier = Modifier.basicMarquee(),
  )
}
