package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.days_ago
import com.trm.audiofeels.core.ui.resources.hours_ago
import com.trm.audiofeels.core.ui.resources.minutes_ago
import com.trm.audiofeels.core.ui.resources.moments_ago
import com.trm.audiofeels.core.ui.resources.one_hour_ago
import com.trm.audiofeels.core.ui.resources.one_minute_ago
import com.trm.audiofeels.core.ui.resources.yesterday
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlaylistLazyRowItem(
  name: String,
  artworkUrl: String?,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(onClick = onClick, modifier = modifier) {
    PlaylistArtworkImage(
      name = name,
      artworkUrl = artworkUrl,
      modifier = { PlaylistLazyRowItemArtworkImageModifier(it) },
    )
    PlaylistNameSpacedText(name)
  }
}

@Composable
fun PlaylistLazyVerticalGridItem(
  name: String,
  artworkUrl: String?,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(onClick = onClick, modifier = modifier) {
    PlaylistArtworkImage(
      name = name,
      artworkUrl = artworkUrl,
      modifier = { PlaylistLazyVerticalGridItemArtworkImageModifier(it) },
    )
    PlaylistNameSpacedText(name)
  }
}

@Composable
private fun ColumnScope.PlaylistNameSpacedText(name: String) {
  Spacer(modifier = Modifier.height(8.dp))
  PlaylistNameText(name)
  Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun PlaylistArtworkImage(
  name: String,
  artworkUrl: String?,
  modifier: @Composable (Boolean) -> Modifier,
) {
  AsyncShimmerImage(model = artworkUrl, contentDescription = name, modifier = modifier)
}

@Composable
fun PlaylistLazyRowItemArtworkImageModifier(shimmerEnabled: Boolean): Modifier =
  Modifier.size(150.dp)
    .clip(RoundedCornerShape(16.dp))
    .shimmerBackground(enabled = shimmerEnabled, shape = RoundedCornerShape(16.dp))

@Composable
fun PlaylistLazyVerticalGridItemArtworkImageModifier(shimmerEnabled: Boolean): Modifier =
  Modifier.fillMaxWidth()
    .aspectRatio(1f)
    .clip(RoundedCornerShape(16.dp))
    .shimmerBackground(enabled = shimmerEnabled, shape = RoundedCornerShape(16.dp))

@Composable
fun PlaylistNameText(name: String) {
  Text(
    text = name,
    style = MaterialTheme.typography.labelLarge,
    maxLines = 1,
    modifier = Modifier.padding(horizontal = 8.dp).basicMarquee(),
  )
}

@Composable
fun ColumnScope.PlaylistPlaceholderItemContent() {
  Spacer(modifier = Modifier.height(158.dp))
  Text(text = "", style = MaterialTheme.typography.labelLarge)
  Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun LazyGridItemScope.LazyGridPlaylistPlaceholderItem(content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier =
      Modifier.width(150.dp)
        .shimmerBackground(enabled = true, shape = RoundedCornerShape(16.dp))
        .animateItem(),
    content = content,
  )
}

@Composable
fun ColumnScope.CarryOnPlaylistPlaceholderItemContent() {
  Spacer(modifier = Modifier.height(158.dp))
  Text(text = "", style = MaterialTheme.typography.labelLarge)
  Text(text = "", style = MaterialTheme.typography.labelSmall)
  Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun CarryOnPlaylistLazyRowItem(
  name: String,
  artworkUrl: String?,
  lastPlayed: Instant,
  now: Instant = Clock.System.now(),
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(onClick = onClick, modifier = modifier) {
    PlaylistArtworkImage(
      name = name,
      artworkUrl = artworkUrl,
      modifier = { PlaylistLazyRowItemArtworkImageModifier(it) },
    )
    CarryOnPlaylistBody(name = name, lastPlayed = lastPlayed, now = now)
  }
}

@Composable
fun CarryOnPlaylistLazyVerticalGridItem(
  name: String,
  artworkUrl: String?,
  lastPlayed: Instant,
  now: Instant = Clock.System.now(),
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(onClick = onClick, modifier = modifier) {
    PlaylistArtworkImage(
      name = name,
      artworkUrl = artworkUrl,
      modifier = { PlaylistLazyVerticalGridItemArtworkImageModifier(it) },
    )
    CarryOnPlaylistBody(name = name, lastPlayed = lastPlayed, now = now)
  }
}

@Composable
private fun ColumnScope.CarryOnPlaylistBody(
  name: String,
  lastPlayed: Instant,
  now: Instant = Clock.System.now(),
) {
  Spacer(modifier = Modifier.height(8.dp))
  PlaylistNameText(name = name)
  PlaylistLastPlayedAgoText(duration = now - lastPlayed)
  Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun PlaylistLastPlayedAgoText(duration: Duration) {
  Text(
    text =
      when {
        duration.inWholeDays > 1L -> {
          stringResource(Res.string.days_ago, duration.inWholeDays)
        }
        duration.inWholeDays == 1L -> {
          stringResource(Res.string.yesterday)
        }
        duration.inWholeHours > 1L -> {
          stringResource(Res.string.hours_ago, duration.inWholeHours)
        }
        duration.inWholeHours == 1L -> {
          stringResource(Res.string.one_hour_ago)
        }
        duration.inWholeMinutes > 1L -> {
          stringResource(Res.string.minutes_ago, duration.inWholeMinutes)
        }
        duration.inWholeMinutes == 1L -> {
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
