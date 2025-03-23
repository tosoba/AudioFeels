package com.trm.audiofeels.ui.player.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.cancel_playback
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlayerCancelPlaybackButton(
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  onClick: () -> Unit,
) {
  IconButton(modifier = modifier, enabled = enabled, onClick = onClick) {
    Icon(
      imageVector = Icons.Outlined.Close,
      contentDescription = stringResource(Res.string.cancel_playback),
    )
  }
}
