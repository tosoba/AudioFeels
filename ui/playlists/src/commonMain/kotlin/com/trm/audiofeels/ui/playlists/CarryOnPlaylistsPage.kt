package com.trm.audiofeels.ui.playlists

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.ui.compose.BottomEdgeGradient
import com.trm.audiofeels.core.ui.compose.CarryOnPlaylistLazyVerticalGridItem
import com.trm.audiofeels.core.ui.compose.ErrorListItem
import com.trm.audiofeels.core.ui.compose.LazyGridPlaylistPlaceholderItem
import com.trm.audiofeels.core.ui.compose.PlaylistPlaceholderItemContent
import com.trm.audiofeels.core.ui.compose.PlaylistsLazyVerticalGrid
import com.trm.audiofeels.core.ui.compose.TopEdgeGradient
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.carry_on
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CarryOnPlaylistsPage(
  playlists: LoadableState<List<CarryOnPlaylist>>,
  animatedContentScope: AnimatedContentScope,
  hazeState: HazeState,
  bottomSpacerHeight: Dp,
  onPlaylistClick: (CarryOnPlaylist) -> Unit,
  onNavigationIconClick: () -> Unit,
) {
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
          items(playlists.value) {
            CarryOnPlaylistLazyVerticalGridItem(
              name = it.playlist.name,
              artworkUrl = it.playlist.artworkUrl,
              lastPlayed = it.lastPlayed,
              modifier =
                Modifier.sharedElement(
                    state =
                      rememberSharedContentState(
                        key = "${stringResource(Res.string.carry_on)}-${it.playlist.id}"
                      ),
                    animatedVisibilityScope = animatedContentScope,
                  )
                  .animateItem(),
              onClick = { onPlaylistClick(it) },
            )
          }
        }
        is LoadableState.Error -> {
          item(span = { GridItemSpan(maxLineSpan) }) {
            ErrorListItem(modifier = Modifier.animateItem(), onClick = {})
          }
        }
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(bottomSpacerHeight))
      }
    }

    PlaylistsTopBar(
      title = stringResource(Res.string.carry_on),
      hazeState = hazeState,
      onNavigationIconClick = onNavigationIconClick,
    )

    TopEdgeGradient(topOffset = topAppBarSpacerHeight())
    BottomEdgeGradient()
  }
}
