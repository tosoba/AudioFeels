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
class AppLayoutState(
  private val playerVisible: Boolean,
  val playerLayoutState: AppPlayerLayoutState,
) {
  suspend fun onNavigateToPageDestination() {
    if (playerVisible && playerLayoutState.currentSheetValue == SheetValue.Expanded) {
      playerLayoutState.scaffoldState.bottomSheetState.partialExpand()
    }
  }

  suspend fun onSupportingPaneValueChange(paneValue: PaneAdaptedValue) {
    playerLayoutState.supportingPaneValue = paneValue

    when (paneValue) {
      PaneAdaptedValue.Hidden -> {
        if (playerVisible) {
          playerLayoutState.restoreLastVisibleSheetValue()
        }
      }
      PaneAdaptedValue.Expanded -> {
        if (playerVisible) {
          playerLayoutState.scaffoldState.bottomSheetState.hide()
        }
      }
    }
  }
}

@Composable
fun rememberAppLayoutState(
  playerVisible: Boolean,
  playerViewState: AppPlayerLayoutState,
): AppLayoutState {
  val state =
    remember(playerVisible, playerViewState) { AppLayoutState(playerVisible, playerViewState) }

  LaunchedEffect(playerVisible) {
    if (playerVisible) {
      playerViewState.partialExpandSheetIfPaneHidden()
    } else {
      playerViewState.hideSheetIfPaneHidden()
    }
  }

  return state
}
