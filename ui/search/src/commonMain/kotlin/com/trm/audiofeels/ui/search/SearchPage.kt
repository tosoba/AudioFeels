package com.trm.audiofeels.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.trm.audiofeels.core.ui.compose.PlaylistLazyVerticalGridItem
import com.trm.audiofeels.core.ui.compose.PlaylistPlaceholderItemContent
import com.trm.audiofeels.core.ui.compose.TopEdgeGradient
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
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
  val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
  val result by viewModel.result.collectAsStateWithLifecycle()

  Box {
    LazyVerticalGrid(
      modifier = Modifier.fillMaxSize().hazeSource(hazeState),
      columns = GridCells.Adaptive(150.dp),
      contentPadding = PaddingValues(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(topAppBarSpacerHeight()))
      }

      when (val searchResult = result) {
        LoadableState.Loading -> {
          items(50) { SearchListPlaceholderItem { PlaylistPlaceholderItemContent() } }
        }
        is LoadableState.Idle -> {
          when {
            searchResult.value.query.isEmpty() -> {
              item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                  text = "Query is empty",
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth().animateItem(),
                )
              }
            }
            searchResult.value.query.length < 3 -> {
              item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                  text = "Query is too short",
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth().animateItem(),
                )
              }
            }
            searchResult.value.playlists.isEmpty() -> {
              item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                  text = "No playlists found",
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth().animateItem(),
                )
              }
            }
            else -> {
              items(searchResult.value.playlists) {
                PlaylistLazyVerticalGridItem(
                  name = it.name,
                  artworkUrl = it.artworkUrl,
                  modifier = Modifier.animateItem(),
                  onClick = { onPlaylistClick(it) },
                )
              }
            }
          }
        }
        is LoadableState.Error -> {
          item(span = { GridItemSpan(maxLineSpan) }) {
            ErrorListItem(modifier = Modifier.animateItem(), onClick = viewModel.result::restart)
          }
        }
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(bottomSpacerHeight))
      }
    }

    TopEdgeGradient(topOffset = topAppBarSpacerHeight() + 24.dp)
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

@Composable
private fun LazyGridItemScope.SearchListPlaceholderItem(
  content: @Composable ColumnScope.() -> Unit
) {
  Column(
    modifier =
      Modifier.width(150.dp)
        .shimmerBackground(enabled = true, shape = RoundedCornerShape(16.dp))
        .animateItem(),
    content = content,
  )
}
