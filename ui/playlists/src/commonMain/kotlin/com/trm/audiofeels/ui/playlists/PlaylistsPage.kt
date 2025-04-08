package com.trm.audiofeels.ui.playlists

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.ui.compose.BottomEdgeGradient
import com.trm.audiofeels.core.ui.compose.ErrorListItem
import com.trm.audiofeels.core.ui.compose.LazyGridPlaylistPlaceholderItem
import com.trm.audiofeels.core.ui.compose.PlaylistLazyVerticalGridItem
import com.trm.audiofeels.core.ui.compose.PlaylistPlaceholderItemContent
import com.trm.audiofeels.core.ui.compose.PlaylistsLazyVerticalGrid
import com.trm.audiofeels.core.ui.compose.ShufflePlayRandomButtonsColumn
import com.trm.audiofeels.core.ui.compose.TopEdgeGradient
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
import com.trm.audiofeels.domain.model.Playlist
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlin.random.Random

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistsPage(
  playlists: LoadableState<List<Playlist>>,
  title: String,
  animatedContentScope: AnimatedContentScope,
  hazeState: HazeState,
  bottomSpacerHeight: Dp,
  sharedElementKeyPrefix: String,
  showFABs: Boolean,
  onPlaylistClick: (Playlist) -> Unit,
  onNavigationIconClick: () -> Unit,
  onRetryClick: () -> Unit,
) {
  var playlistIndices by
    rememberSaveable(playlists) {
      mutableStateOf(playlists.valueOrNull?.indices?.toList() ?: emptyList())
    }
  var fabsHeightPx by rememberSaveable { mutableIntStateOf(0) }

  Box {
    PlaylistsLazyVerticalGrid(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(topAppBarSpacerHeight()))
      }

      when (playlists) {
        LoadableState.Loading -> {
          items(50) { LazyGridPlaylistPlaceholderItem { PlaylistPlaceholderItemContent() } }
        }
        is LoadableState.Idle -> {
          fun playlistForIndexAt(index: Int): Playlist = playlists.value[playlistIndices[index]]

          items(count = playlists.value.size, key = { playlistForIndexAt(it).id }) {
            val playlist = playlistForIndexAt(it)
            PlaylistLazyVerticalGridItem(
              name = playlist.name,
              artworkUrl = playlist.artworkUrl,
              modifier =
                Modifier.sharedElement(
                    state =
                      rememberSharedContentState(key = "$sharedElementKeyPrefix-${playlist.id}"),
                    animatedVisibilityScope = animatedContentScope,
                  )
                  .animateItem(),
              onClick = { onPlaylistClick(playlist) },
            )
          }
        }
        is LoadableState.Error -> {
          item(span = { GridItemSpan(maxLineSpan) }) {
            ErrorListItem(modifier = Modifier.animateItem(), onClick = onRetryClick)
          }
        }
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

    AnimatedVisibility(
      visible = showFABs && !playlists.valueOrNull.isNullOrEmpty(),
      modifier =
        Modifier.align(Alignment.BottomEnd).padding(16.dp).onSizeChanged {
          fabsHeightPx = it.height
        },
    ) {
      ShufflePlayRandomButtonsColumn(
        onShuffleClick = { playlistIndices = playlistIndices.shuffled() },
        onRandomClick = {
          playlists.valueOrNull?.let { onPlaylistClick(it[Random.nextInt(until = it.size)]) }
        },
      )
    }

    TopEdgeGradient(topOffset = topAppBarSpacerHeight())
    BottomEdgeGradient()
  }
}
