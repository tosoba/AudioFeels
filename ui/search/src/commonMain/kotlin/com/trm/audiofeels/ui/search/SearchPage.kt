package com.trm.audiofeels.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.trm.audiofeels.core.ui.compose.emptyListTextItem
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.no_playlists_found_primary_text
import com.trm.audiofeels.core.ui.resources.no_playlists_found_search_secondary_text
import com.trm.audiofeels.core.ui.resources.query_is_empty_primary_text
import com.trm.audiofeels.core.ui.resources.query_is_empty_secondary_text
import com.trm.audiofeels.core.ui.resources.query_is_too_short_primary_text
import com.trm.audiofeels.core.ui.resources.query_is_too_short_secondary_text
import com.trm.audiofeels.domain.model.Playlist
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

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

  var showSearchBarContentSpacerItem by remember { mutableStateOf(false) }

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

      if (showSearchBarContentSpacerItem) {
        item(span = { GridItemSpan(maxLineSpan) }) {
          Spacer(modifier = Modifier.height(SEARCH_TOP_BAR_CONTENT_HEIGHT - 16.dp).animateItem())
        }
      }

      when {
        query.isEmpty() -> {
          emptyListTextItem(
            primaryText = Res.string.query_is_empty_primary_text,
            secondaryText = Res.string.query_is_empty_secondary_text,
          )
        }
        query.length < 3 -> {
          emptyListTextItem(
            primaryText = Res.string.query_is_too_short_primary_text,
            secondaryText = Res.string.query_is_too_short_secondary_text,
          )
        }
        else -> {
          when (val playlists = playlistsState) {
            LoadableState.Loading -> {
              items(50) { LazyGridPlaylistPlaceholderItem { PlaylistPlaceholderItemContent() } }
            }
            is LoadableState.Idle -> {
              if (playlists.value.isEmpty()) {
                emptyListTextItem(
                  primaryText = Res.string.no_playlists_found_primary_text,
                  secondaryText = Res.string.no_playlists_found_search_secondary_text,
                )
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
      onSearchBarExpandedChange = { showSearchBarContentSpacerItem = it },
    )
  }
}
