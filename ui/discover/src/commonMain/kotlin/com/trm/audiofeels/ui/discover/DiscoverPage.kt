package com.trm.audiofeels.ui.discover

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trm.audiofeels.core.base.model.LoadableState

@Composable
fun DiscoverPage(modifier: Modifier = Modifier, viewModel: DiscoverViewModel) {
  val playlists by viewModel.playlists.collectAsStateWithLifecycle()
  Crossfade(targetState = playlists, modifier = modifier) {
    when (it) {
      LoadableState.Loading -> {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          CircularProgressIndicator()
        }
      }
      is LoadableState.Success -> {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 12.dp)) {
          items(it.value) { playlist -> Text(playlist.name) }
        }
      }
      is LoadableState.Error -> {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Button(onClick = viewModel::loadPlaylists) { Text("Retry") }
        }
      }
    }
  }
}
