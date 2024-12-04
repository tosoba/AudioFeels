package com.trm.audiofeels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Stable
class AppPlayerState(isPlaying: Boolean, sheetValue: SheetValue = SheetValue.PartiallyExpanded) {
  var isPlaying: Boolean by mutableStateOf(isPlaying)
  var lastVisibleSheetValue: SheetValue by mutableStateOf(sheetValue)

  companion object {
    val Saver =
      listSaver<AppPlayerState, Any>(
        save = { state -> listOf(state.isPlaying, state.lastVisibleSheetValue) },
        restore = { restored -> AppPlayerState(restored[0] as Boolean, restored[1] as SheetValue) },
      )
  }
}
