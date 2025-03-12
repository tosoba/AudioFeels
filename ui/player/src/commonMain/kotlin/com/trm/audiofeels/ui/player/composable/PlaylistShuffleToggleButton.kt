package com.trm.audiofeels.ui.player.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.ShuffleOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.turn_off_shuffle
import com.trm.audiofeels.core.ui.resources.turn_on_shuffle
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PlaylistFavouriteShuffleButton(
  checked: Boolean,
  enabled: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  IconToggleButton(checked = false, enabled = enabled, onCheckedChange = onCheckedChange) {
    Icon(
      imageVector = if (checked) Icons.Outlined.ShuffleOn else Icons.Outlined.Shuffle,
      contentDescription =
        stringResource(if (checked) Res.string.turn_off_shuffle else Res.string.turn_on_shuffle),
    )
  }
}
