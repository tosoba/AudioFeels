package com.trm.audiofeels.ui.mood

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.ui.compose.BottomEdgeGradient
import com.trm.audiofeels.core.ui.compose.ErrorListItem
import com.trm.audiofeels.core.ui.compose.LazyGridPlaylistPlaceholderItem
import com.trm.audiofeels.core.ui.compose.PlaylistLazyVerticalGridItem
import com.trm.audiofeels.core.ui.compose.PlaylistPlaceholderItemContent
import com.trm.audiofeels.core.ui.compose.PlaylistsLazyVerticalGrid
import com.trm.audiofeels.core.ui.compose.TopEdgeGradient
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
import com.trm.audiofeels.domain.model.Playlist
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun MoodPage(
  viewModel: MoodViewModel,
  bottomSpacerHeight: Dp,
  onPlaylistClick: (Playlist) -> Unit,
  onNavigationIconClick: () -> Unit,
) {
  val hazeState = remember(::HazeState)
  val playlists by viewModel.playlists.collectAsStateWithLifecycle()

  Box {
    PlaylistsLazyVerticalGrid(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(topAppBarSpacerHeight()))
      }

      when (playlists) {
        LoadableState.Loading -> {
          items(50) { LazyGridPlaylistPlaceholderItem { PlaylistPlaceholderItemContent() } }
        }
        is LoadableState.Idle<*> -> {
          val items = playlists.valueOrNull
          if (items.isNullOrEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
              Text(
                text = "No playlists found",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().animateItem(),
              )
            }
          } else {
            items(items) {
              PlaylistLazyVerticalGridItem(
                name = it.name,
                artworkUrl = it.artworkUrl,
                modifier = Modifier.animateItem(),
                onClick = { onPlaylistClick(it) },
              )
            }
          }
        }
        is LoadableState.Error ->
          item(span = { GridItemSpan(maxLineSpan) }) {
            ErrorListItem(modifier = Modifier.animateItem(), onClick = viewModel.playlists::restart)
          }
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(bottomSpacerHeight))
      }
    }

    MoodTopBar(
      mood = viewModel.mood,
      hazeState = hazeState,
      onNavigationIconClick = onNavigationIconClick,
    )

    TopEdgeGradient(topOffset = topAppBarSpacerHeight())
    BottomEdgeGradient()
  }
}
