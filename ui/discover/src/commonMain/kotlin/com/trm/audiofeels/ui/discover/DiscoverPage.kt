package com.trm.audiofeels.ui.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.ui.compose.AsyncShimmerImage
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.carry_on
import com.trm.audiofeels.core.ui.resources.days_ago
import com.trm.audiofeels.core.ui.resources.error_occurred
import com.trm.audiofeels.core.ui.resources.hours_ago
import com.trm.audiofeels.core.ui.resources.minutes_ago
import com.trm.audiofeels.core.ui.resources.moments_ago
import com.trm.audiofeels.core.ui.resources.one_hour_ago
import com.trm.audiofeels.core.ui.resources.one_minute_ago
import com.trm.audiofeels.core.ui.resources.refresh
import com.trm.audiofeels.core.ui.resources.trending
import com.trm.audiofeels.core.ui.resources.yesterday
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun DiscoverPage(
  viewModel: DiscoverViewModel,
  onCarryPlaylistClick: (CarryOnPlaylist) -> Unit,
  onTrendingPlaylistClick: (Playlist) -> Unit,
) {
  val carryOnPlaylists by viewModel.carryOnPlaylists.collectAsStateWithLifecycle()
  val trendingPlaylists by viewModel.trendingPlaylists.collectAsStateWithLifecycle()

  Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    DiscoverListHeadline(
      text = stringResource(Res.string.carry_on),
      list = carryOnPlaylists,
      modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
    )

    DiscoverListLazyRow(
      list = carryOnPlaylists,
      onRetryClick = {},
      placeholderItemContent = {
        Spacer(modifier = Modifier.height(158.dp))
        Text(text = "", style = MaterialTheme.typography.labelLarge)
        Text(text = "", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(8.dp))
      },
    ) { index, lastIndex, carryOn ->
      CarryOnPlaylistItem(
        carryOn = carryOn,
        modifier =
          Modifier.width(150.dp)
            .padding(playlistItemPaddingValues(itemIndex = index, lastIndex = lastIndex))
            .animateItem(),
        onClick = onCarryPlaylistClick,
      )
    }

    DiscoverListHeadline(
      text = stringResource(Res.string.trending),
      list = trendingPlaylists,
      modifier = Modifier.padding(horizontal = 12.dp),
    )

    DiscoverListLazyRow(
      list = trendingPlaylists,
      onRetryClick = viewModel.trendingPlaylists::restart,
      placeholderItemContent = {
        Spacer(modifier = Modifier.height(158.dp))
        Text(text = "", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
      },
    ) { index, lastIndex, playlist ->
      PlaylistItem(
        playlist = playlist,
        modifier =
          Modifier.width(150.dp)
            .padding(playlistItemPaddingValues(itemIndex = index, lastIndex = lastIndex))
            .animateItem(),
        onClick = onTrendingPlaylistClick,
      )
    }
  }
}

@Composable
private fun <T : Any> DiscoverListHeadline(
  text: String,
  list: LoadableState<List<T>>,
  modifier: Modifier = Modifier,
  shimmerShape: Shape = RoundedCornerShape(6.dp),
) {
  Crossfade(list) {
    when (it) {
      is LoadableState.Loading -> {
        Box(modifier.shimmerBackground(enabled = true, shape = shimmerShape)) {
          Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.Transparent),
          )
        }
      }
      is LoadableState.Idle -> {
        if (!it.valueOrNull.isNullOrEmpty()) {
          Text(text = text, style = MaterialTheme.typography.headlineSmall, modifier = modifier)
        }
      }
      is LoadableState.Error -> {}
    }
  }
}

@Composable
private fun <T : Any> DiscoverListLazyRow(
  list: LoadableState<List<T>>,
  onRetryClick: () -> Unit,
  placeholderItemContent: @Composable ColumnScope.() -> Unit,
  item: @Composable LazyItemScope.(Int, Int, T) -> Unit,
) {
  AnimatedVisibility(visible = list.discoverListVisible(), enter = fadeIn(), exit = fadeOut()) {
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(12.dp),
      userScrollEnabled = list !is LoadableState.Error,
    ) {
      when (list) {
        LoadableState.Loading -> {
          val count = 50
          items(count) { index ->
            DiscoverListPlaceholderItem(
              index = index,
              lastIndex = count - 1,
              content = placeholderItemContent,
            )
          }
        }
        is LoadableState.Idle -> {
          itemsIndexed(list.value) { index, carryOn -> item(index, list.value.lastIndex, carryOn) }
        }
        is LoadableState.Error -> {
          item { DiscoverListErrorItem(onClick = onRetryClick) }
        }
      }
    }
  }
}

@Composable
private fun LazyItemScope.DiscoverListPlaceholderItem(
  index: Int,
  lastIndex: Int,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(
    modifier =
      Modifier.width(150.dp)
        .padding(playlistItemPaddingValues(itemIndex = index, lastIndex = lastIndex))
        .shimmerBackground(enabled = true, shape = RoundedCornerShape(12.dp))
        .animateItem(),
    content = content,
  )
}

@Composable
private fun LazyItemScope.DiscoverListErrorItem(onClick: () -> Unit) {
  Box(modifier = Modifier.fillParentMaxWidth().animateItem(), contentAlignment = Alignment.Center) {
    Card(modifier = Modifier.width(150.dp), onClick = onClick) {
      Image(
        painter = rememberVectorPainter(vectorResource(Res.drawable.refresh)),
        contentDescription = null,
        modifier =
          Modifier.size(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer),
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = stringResource(Res.string.error_occurred),
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).basicMarquee(),
      )

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}

private fun <T : Any> LoadableState<List<T>>.discoverListVisible(): Boolean =
  this !is LoadableState.Idle || !valueOrNull.isNullOrEmpty()

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
    PlaylistLastPlayedAgoText(duration = now - carryOn.lastPlayed)
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
    style = MaterialTheme.typography.labelLarge,
    maxLines = 1,
    modifier = Modifier.padding(horizontal = 8.dp).basicMarquee(),
  )
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

private fun playlistItemPaddingValues(itemIndex: Int, lastIndex: Int): PaddingValues =
  PaddingValues(
    start = if (itemIndex > 0) 6.dp else 0.dp,
    end = if (itemIndex < lastIndex) 6.dp else 0.dp,
  )
