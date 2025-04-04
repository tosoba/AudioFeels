package com.trm.audiofeels.ui.search

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.no_playlists_found
import com.trm.audiofeels.domain.model.Playlist
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchPage(
  viewModel: SearchViewModel,
  hazeState: HazeState,
  bottomSpacerHeight: Dp,
  onPlaylistClick: (Playlist) -> Unit,
) {
  val query by viewModel.query.collectAsStateWithLifecycle()
  val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
  val playlistsState by viewModel.playlists.collectAsStateWithLifecycle()

  Box {
    PlaylistsLazyVerticalGrid(
      modifier = Modifier.fillMaxSize().hazeSource(hazeState),
      singleItem =
        query.length < 3 ||
          (playlistsState is LoadableState.Idle && playlistsState.valueOrNull.isNullOrEmpty()),
    ) {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(topAppBarSpacerHeight()))
      }

      when {
        query.isEmpty() -> {
          item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
              text = "Query is empty",
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth(),
            )
          }
        }
        query.length < 3 -> {
          item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
              text = "Query is too short",
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth(),
            )
          }
        }
        else -> {
          when (val playlists = playlistsState) {
            LoadableState.Loading -> {
              items(50) { LazyGridPlaylistPlaceholderItem { PlaylistPlaceholderItemContent() } }
            }
            is LoadableState.Idle -> {
              if (playlists.value.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                  Text(
                    text = stringResource(Res.string.no_playlists_found),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                  )
                }
              } else {
                items(playlists.value) {
                  PlaylistLazyVerticalGridItem(
                    name = it.name,
                    artworkUrl = it.artworkUrl,
                    modifier = Modifier.animateItem(),
                    onClick = { onPlaylistClick(it) },
                  )
                }
              }
            }
            is LoadableState.Error -> {
              item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorListItem(onClick = viewModel.playlists::restart)
              }
            }
          }
        }
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(bottomSpacerHeight))
      }
    }

    TopEdgeGradient(topOffset = topAppBarSpacerHeight() + 8.dp)
    BottomEdgeGradient()

    SearchTopBar(
      hazeState = hazeState,
      suggestions = suggestions,
      onQueryChange = viewModel::onQueryChange,
      onSearchBarExpandedChange = {
        // TODO: get searchBar height and increase top space height if expanded
      },
    )
  }
}
