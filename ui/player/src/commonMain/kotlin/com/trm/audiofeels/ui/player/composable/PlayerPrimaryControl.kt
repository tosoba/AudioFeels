package com.trm.audiofeels.ui.player.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.trm.audiofeels.ui.player.PlayerPrimaryControlState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PlayerPrimaryControl(controlState: PlayerPrimaryControlState) {
  FilledIconButton(
    onClick = { (controlState as? PlayerPrimaryControlState.Action)?.action?.invoke() },
    enabled = controlState is PlayerPrimaryControlState.Action,
  ) {
    AnimatedContent(controlState) { state ->
      when (state) {
        is PlayerPrimaryControlState.Action -> {
          Icon(
            imageVector = state.imageVector,
            contentDescription = state.contentDescription?.let { stringResource(it) },
          )
        }
        PlayerPrimaryControlState.Loading -> {
          CircularProgressIndicator()
        }
      }
    }
  }
}
