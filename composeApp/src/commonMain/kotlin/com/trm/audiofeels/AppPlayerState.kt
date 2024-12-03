package com.trm.audiofeels

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue

@Stable
class AppPlayerState(isPlaying: Boolean) {
  var isPlaying: Boolean by mutableStateOf(isPlaying)

  companion object {
    val Saver =
      listSaver<AppPlayerState, Any>(
        save = { state -> listOf(state.isPlaying) },
        restore = { restored -> AppPlayerState(restored[0] as Boolean) },
      )
  }
}
