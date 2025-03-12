package com.trm.audiofeels.ui.player.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.add_to_favourites
import com.trm.audiofeels.core.ui.resources.remove_from_favourites
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PlaylistFavouriteToggleButton(
  checked: Boolean,
  enabled: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  IconToggleButton(checked = false, enabled = enabled, onCheckedChange = onCheckedChange) {
    Icon(
      imageVector = if (checked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription =
        stringResource(
          if (checked) Res.string.remove_from_favourites else Res.string.add_to_favourites
        ),
    )
  }
}
