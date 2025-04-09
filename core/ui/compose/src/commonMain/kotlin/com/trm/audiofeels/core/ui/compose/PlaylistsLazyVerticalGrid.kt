package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.compose.theme.ListItemSize
import com.trm.audiofeels.core.ui.compose.theme.Spacing

@Composable
fun PlaylistsLazyVerticalGrid(
  modifier: Modifier = Modifier,
  singleItem: Boolean = false,
  content: LazyGridScope.() -> Unit,
) {
  LazyVerticalGrid(
    modifier = modifier,
    columns = GridCells.Adaptive(ListItemSize.medium150dp),
    contentPadding = PaddingValues(horizontal = Spacing.medium16dp),
    horizontalArrangement = Arrangement.spacedBy(Spacing.medium16dp),
    verticalArrangement =
      if (singleItem) Arrangement.Center else Arrangement.spacedBy(Spacing.medium16dp),
    content = content,
  )
}
