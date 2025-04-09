package com.trm.audiofeels.core.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.compose.theme.Spacing
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.play_random
import com.trm.audiofeels.core.ui.resources.play_random_playlist
import com.trm.audiofeels.core.ui.resources.shuffle_playlists
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShufflePlayRandomButtonsColumn(
  modifier: Modifier = Modifier,
  onShuffleClick: () -> Unit,
  onRandomClick: () -> Unit,
) {
  Column(horizontalAlignment = Alignment.End, modifier = modifier) {
    SmallFloatingActionButton(onClick = onShuffleClick) {
      Icon(
        imageVector = Icons.Outlined.Shuffle,
        contentDescription = stringResource(Res.string.shuffle_playlists),
      )
    }

    Spacer(modifier = Modifier.height(Spacing.medium16dp))

    PlayRandomFloatingActionButton(onClick = onRandomClick)
  }
}

@Composable
fun PlayRandomFloatingActionButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
  ExtendedFloatingActionButton(
    onClick = onClick,
    containerColor = MaterialTheme.colorScheme.inversePrimary,
    text = {
      Text(stringResource(Res.string.play_random), style = MaterialTheme.typography.titleMedium)
    },
    icon = {
      Icon(
        imageVector = Icons.Filled.PlayArrow,
        contentDescription = stringResource(Res.string.play_random_playlist),
        modifier = Modifier.size(Spacing.large32dp),
      )
    },
    modifier = modifier,
  )
}
