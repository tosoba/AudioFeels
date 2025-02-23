package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.Crossfade
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.trm.audiofeels.ui.player.PlayerViewState

@Composable
internal fun PlayerPrimaryControl(controlState: PlayerViewState.PrimaryControlState) {
  Crossfade(controlState) {
    when (it) {
      PlayerViewState.PrimaryControlState.Loading -> {
        CircularProgressIndicator()
      }
      is PlayerViewState.PrimaryControlState.Action -> {
        FilledIconButton(onClick = it.action) {
          Icon(imageVector = it.imageVector, contentDescription = it.contentDescription)
        }
      }
    }
  }
}
