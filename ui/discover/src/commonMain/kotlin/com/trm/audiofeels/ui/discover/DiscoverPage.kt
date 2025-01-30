package com.trm.audiofeels.ui.discover

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.artwork_placeholder
import com.trm.audiofeels.domain.model.Playlist
import org.jetbrains.compose.resources.painterResource

@Composable
fun DiscoverPage(
  viewModel: DiscoverViewModel,
  modifier: Modifier = Modifier,
  onPlaylistClick: (Playlist) -> Unit,
) {
  val carryOnPlaylists by viewModel.carryOnPlaylists.collectAsStateWithLifecycle()
  val trendingPlaylists by viewModel.trendingPlaylists.collectAsStateWithLifecycle()

  Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    Text(
      text = "Carry on",
      style = MaterialTheme.typography.headlineSmall,
      modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
    )
    Crossfade(targetState = carryOnPlaylists, modifier = modifier) {
      when (it) {
        LoadableState.Loading -> {
          LoadingIndicatorBox()
        }
        is LoadableState.Success -> {
          LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(all = 12.dp)) {
            itemsIndexed(it.value) { index, playlist ->
              PlaylistItem(
                playlist = playlist.playlist,
                modifier =
                  Modifier.width(150.dp)
                    .padding(
                      playlistItemPaddingValues(itemIndex = index, lastIndex = it.value.lastIndex)
                    ),
                onPlaylistClick = onPlaylistClick,
              )
            }
          }
        }
        is LoadableState.Error -> {
          return@Crossfade
        }
      }
    }

    Text(
      text = "Trending",
      style = MaterialTheme.typography.headlineSmall,
      modifier = Modifier.padding(start = 12.dp, end = 12.dp),
    )
    Crossfade(targetState = trendingPlaylists, modifier = modifier) {
      when (it) {
        LoadableState.Loading -> {
          LoadingIndicatorBox()
        }
        is LoadableState.Success -> {
          LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(all = 12.dp)) {
            itemsIndexed(it.value) { index, playlist ->
              PlaylistItem(
                playlist = playlist,
                modifier =
                  Modifier.width(150.dp)
                    .padding(
                      playlistItemPaddingValues(itemIndex = index, lastIndex = it.value.lastIndex)
                    ),
                onPlaylistClick = onPlaylistClick,
              )
            }
          }
        }
        is LoadableState.Error -> {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().height(150.dp),
          ) {
            Button(onClick = viewModel.trendingPlaylists::restart) { Text("Retry") }
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
  onPlaylistClick: (Playlist) -> Unit,
) {
  Card(onClick = { onPlaylistClick(playlist) }, modifier = modifier) {
    if (playlist.artworkUrl != null) {
      AsyncImage(
        model = playlist.artworkUrl,
        contentDescription = playlist.name,
        modifier = Modifier.size(150.dp),
      )
    } else {
      Image(
        painter = painterResource(Res.drawable.artwork_placeholder),
        contentDescription = playlist.name,
        modifier = Modifier.size(150.dp),
      )
    }
    // TODO: text shadow
    Text(text = playlist.name, maxLines = 1, modifier = Modifier.padding(8.dp).basicMarquee())
  }
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
