package com.trm.audiofeels.ui.mood

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
import com.trm.audiofeels.core.ui.compose.ShufflePlayRandomButtonsColumn
import com.trm.audiofeels.core.ui.compose.TopEdgeGradient
import com.trm.audiofeels.core.ui.compose.emptyListTextItem
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.no_playlists_found_mood_secondary_text
import com.trm.audiofeels.core.ui.resources.no_playlists_found_primary_text
import com.trm.audiofeels.domain.model.Playlist
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlin.random.Random

@Composable
fun MoodPage(
  viewModel: MoodViewModel,
  hazeState: HazeState,
  bottomSpacerHeight: Dp,
  showFABs: Boolean,
  onPlaylistClick: (Playlist) -> Unit,
  onNavigationIconClick: () -> Unit,
) {
  val playlists by viewModel.playlists.collectAsStateWithLifecycle()
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
          val items = playlists.valueOrNull
          if (items.isNullOrEmpty()) {
            emptyListTextItem(
              primaryText = Res.string.no_playlists_found_primary_text,
              secondaryText = Res.string.no_playlists_found_mood_secondary_text,
            )
          } else {
            items(items, key = Playlist::id) {
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
            ErrorListItem(modifier = Modifier.animateItem(), onClick = viewModel.playlists::restart)
          }
        }
      }

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(
          modifier =
            Modifier.height(bottomSpacerHeight + with(LocalDensity.current) { fabsHeightPx.toDp() })
        )
      }
    }

    AnimatedVisibility(
      visible = showFABs && !playlists.valueOrNull.isNullOrEmpty(),
      modifier =
        Modifier.align(Alignment.BottomEnd).padding(16.dp).onSizeChanged {
          fabsHeightPx = it.height
        },
    ) {
      ShufflePlayRandomButtonsColumn(
        onShuffleClick = viewModel::onShuffleClick,
        onRandomClick = {
          playlists.valueOrNull?.let { onPlaylistClick(it[Random.nextInt(until = it.size)]) }
        },
      )
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
