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
import androidx.compose.ui.unit.dp

@Composable
fun ShufflePlayRandomButtonsColumn(
  modifier: Modifier = Modifier,
  onShuffleClick: () -> Unit,
  onRandomClick: () -> Unit,
) {
  Column(horizontalAlignment = Alignment.End, modifier = modifier) {
    SmallFloatingActionButton(onClick = onShuffleClick) {
      Icon(imageVector = Icons.Outlined.Shuffle, contentDescription = "Shuffle items")
    }

    Spacer(modifier = Modifier.height(16.dp))

    PlayRandomFloatingActionButton(onClick = onRandomClick)
  }
}

@Composable
fun PlayRandomFloatingActionButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
  ExtendedFloatingActionButton(
    onClick = onClick,
    containerColor = MaterialTheme.colorScheme.inversePrimary,
    text = { Text("Play random", style = MaterialTheme.typography.titleMedium) },
    icon = {
      Icon(
        imageVector = Icons.Filled.PlayArrow,
        contentDescription = "Play a random playlist",
        modifier = Modifier.size(32.dp),
      )
    },
    modifier = modifier,
  )
}
