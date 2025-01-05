package com.trm.audiofeels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Immutable
class AppViewState(private val playerVisible: Boolean, val playerViewState: AppPlayerViewState) {
  suspend fun onNavigateToPageDestination() {
    if (playerVisible && playerViewState.currentSheetValue == SheetValue.Expanded) {
      playerViewState.scaffoldState.bottomSheetState.partialExpand()
    }
  }

  suspend fun onSupportingPaneValueChange(paneValue: PaneAdaptedValue) {
    playerViewState.supportingPaneValue = paneValue

    when (paneValue) {
      PaneAdaptedValue.Hidden -> {
        if (playerVisible) {
          playerViewState.restoreLastVisibleSheetValue()
        }
      }
      PaneAdaptedValue.Expanded -> {
        if (playerVisible) {
          playerViewState.scaffoldState.bottomSheetState.hide()
        }
      }
    }
  }
}

@Composable
fun rememberAppViewState(
  playerVisible: Boolean,
  playerViewState: AppPlayerViewState,
): AppViewState {
  val state =
    remember(playerVisible, playerViewState) { AppViewState(playerVisible, playerViewState) }

  LaunchedEffect(playerVisible) {
    if (playerVisible) {
      playerViewState.partialExpandSheetIfPaneHidden()
    } else {
      playerViewState.hideSheetIfPaneHidden()
    }
  }

  return state
}
