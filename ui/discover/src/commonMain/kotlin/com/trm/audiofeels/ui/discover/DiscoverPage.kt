package com.trm.audiofeels.ui.discover

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowHeightSizeClass
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.ui.compose.BottomEdgeGradient
import com.trm.audiofeels.core.ui.compose.CarryOnPlaylistLazyRowItem
import com.trm.audiofeels.core.ui.compose.CarryOnPlaylistPlaceholderItemContent
import com.trm.audiofeels.core.ui.compose.EndEdgeGradient
import com.trm.audiofeels.core.ui.compose.ErrorListItem
import com.trm.audiofeels.core.ui.compose.MoodItem
import com.trm.audiofeels.core.ui.compose.PlaylistLazyRowItem
import com.trm.audiofeels.core.ui.compose.PlaylistPlaceholderItemContent
import com.trm.audiofeels.core.ui.compose.StartEdgeGradient
import com.trm.audiofeels.core.ui.compose.TopEdgeGradient
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.carry_on
import com.trm.audiofeels.core.ui.resources.favourite
import com.trm.audiofeels.core.ui.resources.mood
import com.trm.audiofeels.core.ui.resources.trending
import com.trm.audiofeels.core.ui.resources.view_all
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DiscoverPage(
  viewModel: DiscoverViewModel,
  animatedContentScope: AnimatedContentScope,
  hazeState: HazeState,
  bottomSpacerHeight: Dp,
  onCarryOnPlaylistClick: (CarryOnPlaylist) -> Unit,
  onPlaylistClick: (Playlist) -> Unit,
  onMoodClick: (Mood) -> Unit,
  onViewAllCarryOnPlaylistsClick: () -> Unit,
  onViewAllFavouritePlaylistsClick: () -> Unit,
  onViewAllTrendingPlaylistsClick: () -> Unit,
  onViewAllMoodsClick: () -> Unit,
) {
  val carryOnPlaylists by viewModel.carryOnPlaylists.collectAsStateWithLifecycle()
  val favouritePlaylists by viewModel.favouritePlaylists.collectAsStateWithLifecycle()
  val trendingPlaylists by viewModel.trendingPlaylists.collectAsStateWithLifecycle()

  Box {
    Column(
      modifier = Modifier.fillMaxSize().hazeSource(hazeState).verticalScroll(rememberScrollState())
    ) {
      Spacer(modifier = Modifier.height(topAppBarSpacerHeight()))

      DiscoverListHeadline(
        text = stringResource(Res.string.carry_on),
        list = carryOnPlaylists,
        onViewAllClick = onViewAllCarryOnPlaylistsClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      )

      DiscoverListLazyRow(
        list = carryOnPlaylists,
        onRetryClick = {},
        placeholderItemContent = { CarryOnPlaylistPlaceholderItemContent() },
      ) { index, lastIndex, carryOn ->
        CarryOnPlaylistLazyRowItem(
          name = carryOn.playlist.name,
          artworkUrl = carryOn.playlist.artworkUrl,
          lastPlayed = carryOn.lastPlayed,
          modifier =
            Modifier.sharedElement(
                state =
                  rememberSharedContentState(
                    key = "${stringResource(Res.string.carry_on)}-${carryOn.playlist.id}"
                  ),
                animatedVisibilityScope = animatedContentScope,
              )
              .width(150.dp)
              .padding(playlistItemPaddingValues(itemIndex = index, lastIndex = lastIndex))
              .animateItem(),
          onClick = { onCarryOnPlaylistClick(carryOn) },
        )
      }

      Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        DiscoverListHeadlineText(
          text = stringResource(Res.string.mood),
          modifier = Modifier.alignByBaseline().basicMarquee(),
        )

        Spacer(modifier = Modifier.weight(1f).padding(horizontal = 4.dp))

        TextButton(onClick = onViewAllMoodsClick, modifier = Modifier.alignByBaseline()) {
          Text(stringResource(Res.string.view_all))
        }
      }

      LazyHorizontalGrid(
        rows = GridCells.FixedSize(90.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
          Modifier.fillMaxWidth()
            .heightIn(
              max =
                when (currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass) {
                  WindowHeightSizeClass.COMPACT -> 124
                  else -> 220
                }.dp
            ),
      ) {
        items(Mood.entries) { item ->
          MoodItem(
            name = item.name,
            symbol = item.symbol,
            modifier =
              Modifier.sharedElement(
                  state = rememberSharedContentState(key = item.name),
                  animatedVisibilityScope = animatedContentScope,
                )
                .size(90.dp),
            onClick = { onMoodClick(item) },
          )
        }
      }

      DiscoverListHeadline(
        text = stringResource(Res.string.favourite),
        list = favouritePlaylists,
        onViewAllClick = onViewAllFavouritePlaylistsClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      )

      DiscoverListLazyRow(
        list = favouritePlaylists,
        onRetryClick = {},
        placeholderItemContent = { PlaylistPlaceholderItemContent() },
      ) { index, lastIndex, playlist ->
        PlaylistLazyRowItem(
          name = playlist.name,
          artworkUrl = playlist.artworkUrl,
          modifier =
            Modifier.sharedElement(
                state =
                  rememberSharedContentState(
                    key = "${stringResource(Res.string.favourite)}-${playlist.id}"
                  ),
                animatedVisibilityScope = animatedContentScope,
              )
              .width(150.dp)
              .padding(playlistItemPaddingValues(itemIndex = index, lastIndex = lastIndex))
              .animateItem(),
          onClick = { onPlaylistClick(playlist) },
        )
      }

      DiscoverListHeadline(
        text = stringResource(Res.string.trending),
        list = trendingPlaylists,
        onViewAllClick = onViewAllTrendingPlaylistsClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      )

      DiscoverListLazyRow(
        list = trendingPlaylists,
        onRetryClick = viewModel.trendingPlaylists::restart,
        placeholderItemContent = { PlaylistPlaceholderItemContent() },
      ) { index, lastIndex, playlist ->
        PlaylistLazyRowItem(
          name = playlist.name,
          artworkUrl = playlist.artworkUrl,
          modifier =
            Modifier.sharedElement(
                state =
                  rememberSharedContentState(
                    key = "${stringResource(Res.string.trending)}-${playlist.id}"
                  ),
                animatedVisibilityScope = animatedContentScope,
              )
              .width(150.dp)
              .padding(playlistItemPaddingValues(itemIndex = index, lastIndex = lastIndex))
              .animateItem(),
          onClick = { onPlaylistClick(playlist) },
        )
      }

      Spacer(modifier = Modifier.height(bottomSpacerHeight))
    }

    DiscoverTopBar(hazeState = hazeState)

    StartEdgeGradient()
    EndEdgeGradient()
    TopEdgeGradient(topOffset = topAppBarSpacerHeight())
    BottomEdgeGradient()
  }
}

@Composable
private fun <T : Any> DiscoverListHeadline(
  text: String,
  list: LoadableState<List<T>>,
  onViewAllClick: () -> Unit,
  modifier: Modifier = Modifier,
  shimmerShape: Shape = RoundedCornerShape(6.dp),
) {
  Crossfade(list) {
    when (it) {
      is LoadableState.Loading -> {
        Row(modifier = modifier) {
          DiscoverListHeadlineText(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(color = Color.Transparent),
            modifier =
              Modifier.shimmerBackground(enabled = true, shape = shimmerShape)
                .alignByBaseline()
                .basicMarquee(),
          )

          Spacer(modifier = Modifier.weight(1f).padding(horizontal = 8.dp))

          Text(
            text = stringResource(Res.string.view_all),
            color = Color.Transparent,
            modifier =
              Modifier.shimmerBackground(enabled = true, shape = shimmerShape).alignByBaseline(),
          )
        }
      }
      is LoadableState.Idle -> {
        Row(modifier = modifier) {
          if (!it.valueOrNull.isNullOrEmpty()) {
            DiscoverListHeadlineText(
              text = text,
              modifier = Modifier.alignByBaseline().basicMarquee(),
            )

            Spacer(modifier = Modifier.weight(1f).padding(horizontal = 8.dp))

            TextButton(onClick = onViewAllClick, modifier = Modifier.alignByBaseline()) {
              Text(stringResource(Res.string.view_all))
            }
          }
        }
      }
      is LoadableState.Error -> {}
    }
  }
}

@Composable
private fun DiscoverListHeadlineText(
  text: String,
  style: TextStyle = MaterialTheme.typography.titleLarge,
  modifier: Modifier = Modifier,
) {
  Text(
    text = text,
    style = style,
    maxLines = 1,
    fontWeight = FontWeight.Medium,
    modifier = modifier,
  )
}

@Composable
private fun <T : Any> DiscoverListLazyRow(
  list: LoadableState<List<T>>,
  onRetryClick: () -> Unit,
  placeholderItemContent: @Composable ColumnScope.() -> Unit,
  item: @Composable LazyItemScope.(Int, Int, T) -> Unit,
) {
  AnimatedVisibility(visible = list.discoverListVisible()) {
    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
      userScrollEnabled = list !is LoadableState.Error,
    ) {
      when (list) {
        LoadableState.Loading -> {
          val count = 50
          items(count) { index ->
            DiscoverListPlaceholderItem(
              index = index,
              lastIndex = count - 1,
              content = placeholderItemContent,
            )
          }
        }
        is LoadableState.Idle -> {
          itemsIndexed(list.value) { index, carryOn -> item(index, list.value.lastIndex, carryOn) }
        }
        is LoadableState.Error -> {
          item {
            ErrorListItem(
              modifier = Modifier.fillParentMaxWidth().animateItem(),
              onClick = onRetryClick,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LazyItemScope.DiscoverListPlaceholderItem(
  index: Int,
  lastIndex: Int,
  content: @Composable ColumnScope.() -> Unit,
) {
  val horizontalPaddingValues = playlistItemPaddingValues(itemIndex = index, lastIndex = lastIndex)
  Column(
    modifier =
      Modifier.width(150.dp)
        .padding(
          top = 16.dp,
          start = horizontalPaddingValues.calculateStartPadding(LocalLayoutDirection.current),
          end = horizontalPaddingValues.calculateEndPadding(LocalLayoutDirection.current),
          bottom = 16.dp,
        )
        .shimmerBackground(enabled = true, shape = RoundedCornerShape(16.dp))
        .animateItem(),
    content = content,
  )
}

private fun <T : Any> LoadableState<List<T>>.discoverListVisible(): Boolean =
  this !is LoadableState.Idle || !valueOrNull.isNullOrEmpty()

private fun playlistItemPaddingValues(itemIndex: Int, lastIndex: Int): PaddingValues =
  PaddingValues(
    start = if (itemIndex > 0) 8.dp else 0.dp,
    end = if (itemIndex < lastIndex) 8.dp else 0.dp,
  )
