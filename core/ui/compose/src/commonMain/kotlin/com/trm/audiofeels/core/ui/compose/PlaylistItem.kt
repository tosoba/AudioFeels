package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.compose.util.shimmerBackground

@Composable
fun PlaylistLazyRowItem(
  name: String,
  artworkUrl: String?,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(onClick = onClick, modifier = modifier) {
    PlaylistArtworkImage(
      name = name,
      artworkUrl = artworkUrl,
      modifier = { PlaylistLazyRowItemArtworkImageModifier(it) },
    )
    PlaylistNameSpacedText(name)
  }
}

@Composable
fun PlaylistLazyVerticalGridItem(
  name: String,
  artworkUrl: String?,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Card(onClick = onClick, modifier = modifier) {
    PlaylistArtworkImage(
      name = name,
      artworkUrl = artworkUrl,
      modifier = { PlaylistLazyVerticalGridItemArtworkImageModifier(it) },
    )
    PlaylistNameSpacedText(name)
  }
}

@Composable
private fun ColumnScope.PlaylistNameSpacedText(name: String) {
  Spacer(modifier = Modifier.height(8.dp))
  PlaylistNameText(name)
  Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun PlaylistArtworkImage(
  name: String,
  artworkUrl: String?,
  modifier: @Composable (Boolean) -> Modifier,
) {
  AsyncShimmerImage(model = artworkUrl, contentDescription = name, modifier = modifier)
}

@Composable
fun PlaylistLazyRowItemArtworkImageModifier(shimmerEnabled: Boolean): Modifier =
  Modifier.size(150.dp)
    .clip(RoundedCornerShape(16.dp))
    .shimmerBackground(enabled = shimmerEnabled, shape = RoundedCornerShape(16.dp))

@Composable
fun PlaylistLazyVerticalGridItemArtworkImageModifier(shimmerEnabled: Boolean): Modifier =
  Modifier.fillMaxWidth()
    .aspectRatio(1f)
    .clip(RoundedCornerShape(16.dp))
    .shimmerBackground(enabled = shimmerEnabled, shape = RoundedCornerShape(16.dp))

@Composable
fun PlaylistNameText(name: String) {
  Text(
    text = name,
    style = MaterialTheme.typography.labelLarge,
    maxLines = 1,
    modifier = Modifier.padding(horizontal = 8.dp).basicMarquee(),
  )
}

@Composable
fun ColumnScope.PlaylistPlaceholderItemContent() {
  Spacer(modifier = Modifier.height(158.dp))
  Text(text = "", style = MaterialTheme.typography.labelLarge)
  Spacer(modifier = Modifier.height(8.dp))
}
