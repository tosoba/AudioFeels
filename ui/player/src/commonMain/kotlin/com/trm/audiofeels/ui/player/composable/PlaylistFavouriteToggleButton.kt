package com.trm.audiofeels.ui.player.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable

@Composable
internal fun PlaylistFavouriteToggleButton(
  checked: Boolean,
  enabled: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  IconToggleButton(checked = false, enabled = enabled, onCheckedChange = onCheckedChange) {
    Icon(
      imageVector =
        if (checked) {
          Icons.Outlined.Favorite
        } else {
          Icons.Outlined.FavoriteBorder
        },
      contentDescription = "Add to favourites",
    )
  }
}
