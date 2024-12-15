package com.trm.audiofeels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.trm.audiofeels.core.player.model.PlayerState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Immutable
class AppViewState(private val playerState: PlayerState, val playerViewState: AppPlayerViewState) {
  suspend fun onNavigateToPageDestination() {
    if (
      playerState is PlayerState.Initialized &&
        playerViewState.currentSheetValue == SheetValue.Expanded
    ) {
      playerViewState.scaffoldState.bottomSheetState.partialExpand()
    }
  }

  suspend fun onSupportingPaneValueChange(paneValue: PaneAdaptedValue) {
    playerViewState.supportingPaneValue = paneValue

    when (paneValue) {
      PaneAdaptedValue.Hidden -> {
        if (playerState is PlayerState.Initialized) {
          playerViewState.restoreLastVisibleSheetValue()
        }
      }
      PaneAdaptedValue.Expanded -> {
        if (playerState is PlayerState.Initialized) {
          playerViewState.scaffoldState.bottomSheetState.hide()
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberAppViewState(
  playerState: PlayerState,
  playerViewState: AppPlayerViewState =
    rememberAppPlayerViewState(
      scaffoldState =
        rememberBottomSheetScaffoldState(
          bottomSheetState =
            rememberStandardBottomSheetState(
              initialValue = SheetValue.Hidden,
              skipHiddenState = false,
            )
        )
    ),
): AppViewState {
  val state = remember(playerState, playerViewState) { AppViewState(playerState, playerViewState) }

  LaunchedEffect(playerState) {
    if (playerState is PlayerState.Initialized) {
      playerViewState.partialExpandSheetIfPaneHidden()
    }
  }

  return state
}
