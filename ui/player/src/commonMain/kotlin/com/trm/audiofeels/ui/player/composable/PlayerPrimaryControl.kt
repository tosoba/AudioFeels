package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.trm.audiofeels.ui.player.PlayerViewState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PlayerPrimaryControl(controlState: PlayerViewState.PrimaryControlState) {
  FilledIconButton(
    onClick = { (controlState as? PlayerViewState.PrimaryControlState.Action)?.action?.invoke() },
    enabled = controlState is PlayerViewState.PrimaryControlState.Action,
  ) {
    AnimatedContent(controlState) { state ->
      when (state) {
        is PlayerViewState.PrimaryControlState.Action -> {
          Icon(
            imageVector = state.imageVector,
            contentDescription = state.contentDescription?.let { stringResource(it) },
          )
        }
        PlayerViewState.PrimaryControlState.Loading -> {
          CircularProgressIndicator()
        }
      }
    }
  }
}
