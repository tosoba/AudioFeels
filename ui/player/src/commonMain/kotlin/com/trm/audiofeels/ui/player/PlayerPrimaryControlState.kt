package com.trm.audiofeels.ui.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.vector.ImageVector
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.pause
import com.trm.audiofeels.core.ui.resources.play
import com.trm.audiofeels.core.ui.resources.retry
import org.jetbrains.compose.resources.StringResource

sealed interface PlayerPrimaryControlState {
  data object Loading : PlayerPrimaryControlState

  data class Action(
    val imageVector: ImageVector,
    val contentDescription: StringResource?,
    val action: () -> Unit,
  ) : PlayerPrimaryControlState

  companion object {
    fun pauseAction(action: () -> Unit): Action =
      Action(
        imageVector = Icons.Filled.Pause,
        contentDescription = Res.string.pause,
        action = action,
      )

    fun playAction(action: () -> Unit): Action =
      Action(
        imageVector = Icons.Filled.PlayArrow,
        contentDescription = Res.string.play,
        action = action,
      )

    fun retryAction(action: () -> Unit): Action =
      Action(
        imageVector = Icons.Filled.Refresh,
        contentDescription = Res.string.retry,
        action = action,
      )
  }
}
