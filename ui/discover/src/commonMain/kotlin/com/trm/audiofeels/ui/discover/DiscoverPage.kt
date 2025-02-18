package com.trm.audiofeels.ui.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.ui.compose.AsyncShimmerImage
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.carry_on
import com.trm.audiofeels.core.ui.resources.days_ago
import com.trm.audiofeels.core.ui.resources.hours_ago
import com.trm.audiofeels.core.ui.resources.minutes_ago
import com.trm.audiofeels.core.ui.resources.moments_ago
import com.trm.audiofeels.core.ui.resources.one_hour_ago
import com.trm.audiofeels.core.ui.resources.one_minute_ago
import com.trm.audiofeels.core.ui.resources.retry
import com.trm.audiofeels.core.ui.resources.trending
import com.trm.audiofeels.core.ui.resources.yesterday
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscoverPage(
  viewModel: DiscoverViewModel,
  onCarryPlaylistClick: (CarryOnPlaylist) -> Unit,
  onTrendingPlaylistClick: (Playlist) -> Unit,
) {
  val carryOnPlaylists by viewModel.carryOnPlaylists.collectAsStateWithLifecycle()
  val trendingPlaylists by viewModel.trendingPlaylists.collectAsStateWithLifecycle()

  Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    // TODO: large retry view

    val carryOnVisible =
      carryOnPlaylists is LoadableState.Loading ||
        (carryOnPlaylists is LoadableState.Idle && !carryOnPlaylists.valueOrNull.isNullOrEmpty())

    Crossfade(carryOnPlaylists) {
      when (carryOnPlaylists) {
        is LoadableState.Loading -> {
          Box(
            Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
              .shimmerBackground(enabled = true, shape = RoundedCornerShape(6.dp))
          ) {
            Text(
              text = stringResource(Res.string.carry_on),
              style = MaterialTheme.typography.headlineSmall.copy(color = Color.Transparent),
            )
          }
        }
        is LoadableState.Idle -> {
          if (!carryOnPlaylists.valueOrNull.isNullOrEmpty()) {
            Text(
              text = stringResource(Res.string.carry_on),
              style = MaterialTheme.typography.headlineSmall,
              modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
            )
          }
        }
        is LoadableState.Error -> {}
      }
    }

    AnimatedVisibility(visible = carryOnVisible) {
      LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(12.dp)) {
        when (val carryOns = carryOnPlaylists) {
          LoadableState.Loading -> {
            val count = 50
            items(count) { index ->
              Box(
                modifier =
                  // TODO: extract placeholder composable for better sizing (consistent with
                  // PlaylistItem which is not a square)
                  Modifier.size(150.dp)
                    .padding(playlistItemPaddingValues(itemIndex = index, lastIndex = count - 1))
                    .shimmerBackground(enabled = true, shape = RoundedCornerShape(12.dp))
                    .animateItem()
              )
            }
          }
          is LoadableState.Idle -> {
            itemsIndexed(carryOns.value) { index, carryOn ->
              CarryOnPlaylistItem(
                carryOn = carryOn,
                modifier =
                  Modifier.width(150.dp)
                    .padding(
                      playlistItemPaddingValues(
                        itemIndex = index,
                        lastIndex = carryOns.value.lastIndex,
                      )
                    )
                    .animateItem(),
                onClick = onCarryPlaylistClick,
              )
            }
          }
          is LoadableState.Error -> {}
        }
      }
    }

    // TODO: use placeholder items
    Text(
      text = stringResource(Res.string.trending),
      style = MaterialTheme.typography.headlineSmall,
      modifier = Modifier.padding(start = 12.dp, end = 12.dp),
    )
    Crossfade(trendingPlaylists) {
      when (it) {
        LoadableState.Loading -> {
          LoadingIndicatorBox()
        }
        is LoadableState.Idle -> {
          LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(all = 12.dp)) {
            itemsIndexed(it.value) { index, playlist ->
              PlaylistItem(
                playlist = playlist,
                modifier =
                  Modifier.width(150.dp)
                    .padding(
                      playlistItemPaddingValues(itemIndex = index, lastIndex = it.value.lastIndex)
                    ),
                onClick = onTrendingPlaylistClick,
              )
            }
          }
        }
        is LoadableState.Error -> {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().height(150.dp),
          ) {
            Button(onClick = viewModel.trendingPlaylists::restart) {
              Text(stringResource(Res.string.retry))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PlaylistItem(
  playlist: Playlist,
  modifier: Modifier = Modifier,
  onClick: (Playlist) -> Unit,
) {
  Card(onClick = { onClick(playlist) }, modifier = modifier) {
    PlaylistArtworkImage(playlist)
    Spacer(modifier = Modifier.height(8.dp))
    PlaylistNameText(playlist)
    Spacer(modifier = Modifier.height(8.dp))
  }
}

@Composable
private fun CarryOnPlaylistItem(
  carryOn: CarryOnPlaylist,
  now: Instant = Clock.System.now(),
  modifier: Modifier = Modifier,
  onClick: (CarryOnPlaylist) -> Unit,
) {
  Card(onClick = { onClick(carryOn) }, modifier = modifier) {
    PlaylistArtworkImage(carryOn.playlist)
    Spacer(modifier = Modifier.height(8.dp))
    PlaylistNameText(carryOn.playlist)
    Spacer(modifier = Modifier.height(4.dp))
    PlaylistLastPlayedAgoText(lastPlayedAgoDuration = carryOn.lastPlayed - now)
    Spacer(modifier = Modifier.height(8.dp))
  }
}

@Composable
private fun PlaylistArtworkImage(playlist: Playlist) {
  AsyncShimmerImage(
    model = playlist.artworkUrl,
    contentDescription = playlist.name,
    modifier = { enabled ->
      Modifier.size(150.dp)
        .clip(RoundedCornerShape(12.dp))
        .shimmerBackground(enabled = enabled, shape = RoundedCornerShape(12.dp))
    },
  )
}

@Composable
private fun PlaylistNameText(playlist: Playlist) {
  Text(
    text = playlist.name,
    style = MaterialTheme.typography.labelMedium,
    maxLines = 1,
    modifier = Modifier.padding(horizontal = 8.dp).basicMarquee(),
  )
}

@Composable
private fun PlaylistLastPlayedAgoText(lastPlayedAgoDuration: Duration) {
  Text(
    text =
      when {
        lastPlayedAgoDuration.inWholeDays > 1L -> {
          stringResource(Res.string.days_ago, lastPlayedAgoDuration.inWholeDays)
        }
        lastPlayedAgoDuration.inWholeDays == 1L -> {
          stringResource(Res.string.yesterday)
        }
        lastPlayedAgoDuration.inWholeHours > 1L -> {
          stringResource(Res.string.hours_ago, lastPlayedAgoDuration.inWholeHours)
        }
        lastPlayedAgoDuration.inWholeHours == 1L -> {
          stringResource(Res.string.one_hour_ago)
        }
        lastPlayedAgoDuration.inWholeMinutes > 1L -> {
          stringResource(Res.string.minutes_ago, lastPlayedAgoDuration.inWholeMinutes)
        }
        lastPlayedAgoDuration.inWholeMinutes == 1L -> {
          stringResource(Res.string.one_minute_ago)
        }
        else -> {
          stringResource(Res.string.moments_ago)
        }
      },
    style = MaterialTheme.typography.labelSmall,
    maxLines = 1,
    modifier = Modifier.padding(horizontal = 8.dp).basicMarquee(),
  )
}

private fun playlistItemPaddingValues(itemIndex: Int, lastIndex: Int): PaddingValues =
  PaddingValues(
    start = if (itemIndex > 0) 6.dp else 0.dp,
    end = if (itemIndex < lastIndex) 6.dp else 0.dp,
  )

@Composable
private fun LoadingIndicatorBox() {
  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(150.dp)) {
    CircularProgressIndicator()
  }
}
