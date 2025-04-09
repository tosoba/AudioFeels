package com.trm.audiofeels.ui.moods

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.compose.BottomEdgeGradient
import com.trm.audiofeels.core.ui.compose.MoodItem
import com.trm.audiofeels.core.ui.compose.TopEdgeGradient
import com.trm.audiofeels.core.ui.compose.theme.Spacing
import com.trm.audiofeels.core.ui.compose.util.topAppBarSpacerHeight
import com.trm.audiofeels.domain.model.Mood
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MoodsPage(
  animatedContentScope: AnimatedContentScope,
  hazeState: HazeState,
  bottomSpacerHeight: Dp,
  onMoodClick: (Mood) -> Unit,
  onNavigationIconClick: () -> Unit,
) {
  Box {
    LazyVerticalGrid(
      columns = GridCells.Adaptive(90.dp),
      contentPadding =
        PaddingValues(
          start = Spacing.medium16dp,
          end = Spacing.medium16dp,
          bottom = Spacing.medium16dp,
        ),
      horizontalArrangement = Arrangement.spacedBy(Spacing.medium16dp),
      verticalArrangement = Arrangement.spacedBy(Spacing.medium16dp),
      modifier = Modifier.fillMaxSize().hazeSource(hazeState),
    ) {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(topAppBarSpacerHeight()))
      }

      items(Mood.entries, key = Mood::name) { item ->
        Box(contentAlignment = Alignment.Center) {
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

      item(span = { GridItemSpan(maxLineSpan) }) {
        Spacer(modifier = Modifier.height(bottomSpacerHeight))
      }
    }

    MoodsTopBar(hazeState = hazeState, onNavigationIconClick = onNavigationIconClick)

    TopEdgeGradient(topOffset = topAppBarSpacerHeight())
    BottomEdgeGradient()
  }
}
