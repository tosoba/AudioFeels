package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground

@Composable
fun LazyGridItemScope.LazyGridPlaylistPlaceholderItem(content: @Composable ColumnScope.() -> Unit) {
  Column(
    modifier =
      Modifier.width(150.dp)
        .shimmerBackground(enabled = true, shape = RoundedCornerShape(16.dp))
        .animateItem(),
    content = content,
  )
}
