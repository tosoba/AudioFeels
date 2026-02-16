package com.trm.audiofeels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.trm.audiofeels.core.ui.compose.util.currentWindowHeightClass
import com.trm.audiofeels.core.ui.compose.util.currentWindowWidthClass
import com.trm.audiofeels.ui.player.PlayerViewState
import com.trm.audiofeels.ui.player.util.playerVisible

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Immutable
class AppLayoutState(
  private val playerVisible: Boolean,
  val playerLayoutState: AppPlayerLayoutState,
  val paneNavigator: ThreePaneScaffoldNavigator<Any>,
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
      else -> {
        if (playerVisible) {
          playerLayoutState.scaffoldState.bottomSheetState.hide()
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberAppLayoutState(playerViewState: PlayerViewState): AppLayoutState {
  val bottomSheetState =
    rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)
  val paneNavigator =
    rememberSupportingPaneScaffoldNavigator(
      scaffoldDirective =
        calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()).let {
          if (
            !playerViewState.playerVisible ||
              currentWindowHeightClass() == WindowHeightSizeClass.Compact ||
              currentWindowWidthClass() == WindowWidthSizeClass.Compact
          ) {
            it.copy(maxHorizontalPartitions = 1, maxVerticalPartitions = 1)
          } else {
            it
          }
        }
    )
  val supportingPaneValue = paneNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
  val playerLayoutState =
    rememberAppPlayerLayoutState(scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState))

  LaunchedEffect(bottomSheetState.currentValue) {
    if (
      bottomSheetState.currentValue == SheetValue.Hidden &&
        supportingPaneValue == PaneAdaptedValue.Hidden
    ) {
      playerViewState.cancelPlayback()
    }
  }

  LaunchedEffect(playerViewState.playerVisible) {
    if (playerViewState.playerVisible) playerLayoutState.partialExpandSheetIfPaneHidden()
    else playerLayoutState.hideSheetIfPaneHidden()
  }

  val state =
    remember(playerViewState.playerVisible, playerLayoutState, paneNavigator) {
      AppLayoutState(playerViewState.playerVisible, playerLayoutState, paneNavigator)
    }

  LaunchedEffect(supportingPaneValue) { state.onSupportingPaneValueChange(supportingPaneValue) }

  return state
}
