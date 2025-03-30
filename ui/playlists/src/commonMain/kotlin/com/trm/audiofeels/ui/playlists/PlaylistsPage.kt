package com.trm.audiofeels.ui.playlists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.trm.audiofeels.core.ui.compose.BottomEdgeGradient
import com.trm.audiofeels.core.ui.compose.PlaylistLazyVerticalGridItem
import com.trm.audiofeels.core.ui.compose.PlaylistsLazyVerticalGrid
import com.trm.audiofeels.core.ui.compose.TopEdgeGradient
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
import com.trm.audiofeels.domain.model.Playlist
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun PlaylistsPage(
  title: String,
  playlists: List<Playlist>,
  bottomSpacerHeight: Dp,
  onPlaylistClick: (Playlist) -> Unit,
  onNavigationIconClick: () -> Unit,
) {
  val hazeState = remember(::HazeState)

  Box {
    PlaylistsLazyVerticalGrid(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(topAppBarSpacerHeight()))
      }

      items(playlists) {
        PlaylistLazyVerticalGridItem(
          name = it.name,
          artworkUrl = it.artworkUrl,
          modifier = Modifier.animateItem(),
          onClick = { onPlaylistClick(it) },
        )
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(bottomSpacerHeight))
      }
    }

    PlaylistsTopBar(
      title = title,
      hazeState = hazeState,
      onNavigationIconClick = onNavigationIconClick,
    )

    TopEdgeGradient(topOffset = topAppBarSpacerHeight())
    BottomEdgeGradient()
  }
}
