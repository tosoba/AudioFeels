package com.trm.audiofeels.ui.discover

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.Playlist
import io.github.aakira.napier.Napier

@Composable
fun DiscoverPage(
  viewModel: DiscoverViewModel,
  modifier: Modifier = Modifier,
  onPlaylistClick: (Playlist) -> Unit,
) {
  val carryOnPlaylists by viewModel.carryOnPlaylists.collectAsStateWithLifecycle()
  LaunchedEffect(carryOnPlaylists) {
    Napier.d(tag = "CARRY_ON", message = carryOnPlaylists.toString())
  }

  val trendingPlaylists by viewModel.trendingPlaylists.collectAsStateWithLifecycle()
  Crossfade(targetState = trendingPlaylists, modifier = modifier) {
    when (it) {
      LoadableState.Loading -> {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          CircularProgressIndicator()
        }
      }
      is LoadableState.Success -> {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 12.dp)) {
          items(it.value) { playlist ->
            Card(onClick = { onPlaylistClick(playlist) }) {
              playlist.artworkUrl?.let { url -> AsyncImage(model = url, contentDescription = null) }
              Text(playlist.name)
            }
          }
        }
      }
      is LoadableState.Error -> {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Button(onClick = viewModel.trendingPlaylists::restart) { Text("Retry") }
        }
      }
    }
  }
}
