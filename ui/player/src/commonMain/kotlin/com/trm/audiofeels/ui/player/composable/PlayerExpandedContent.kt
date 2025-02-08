package com.trm.audiofeels.ui.player.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.audiofeels.ui.player.PlayerViewState

@Composable
fun PlayerExpandedContent(viewState: PlayerViewState, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    LazyColumn {
      when (viewState) {
        is PlayerViewState.Invisible,
        is PlayerViewState.Error,
        is PlayerViewState.Loading -> {}
        is PlayerViewState.Playback -> {
          items(viewState.tracks) { Text(it.title) }
        }
      }
    }
  }
}
