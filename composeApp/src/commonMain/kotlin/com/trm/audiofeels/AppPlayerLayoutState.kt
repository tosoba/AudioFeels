package com.trm.audiofeels

import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Stable
class AppPlayerLayoutState(
  val scaffoldState: BottomSheetScaffoldState,
  lastVisibleSheetValue: SheetValue,
  supportingPaneValue: PaneAdaptedValue,
) {
  var lastVisibleSheetValue: SheetValue by mutableStateOf(lastVisibleSheetValue)
  var supportingPaneValue: PaneAdaptedValue by mutableStateOf(supportingPaneValue)

  val currentSheetValue: SheetValue
    get() = scaffoldState.bottomSheetState.currentValue

  val currentSheetOffset: Float
    get() =
      try {
        scaffoldState.bottomSheetState.requireOffset()
      } catch (ex: Exception) {
        0f
      }

  suspend fun restoreLastVisibleSheetValue() {
    scaffoldState.bottomSheetState.run {
      when (lastVisibleSheetValue) {
        SheetValue.Expanded -> expand()
        SheetValue.PartiallyExpanded -> partialExpand()
        else -> return
      }
    }
  }

  suspend fun partialExpandSheetIfPaneHidden() {
    if (
      supportingPaneValue == PaneAdaptedValue.Hidden && !scaffoldState.bottomSheetState.isVisible
    ) {
      scaffoldState.bottomSheetState.partialExpand()
    }
  }

  suspend fun hideSheetIfPaneHidden() {
    if (
      supportingPaneValue == PaneAdaptedValue.Hidden && scaffoldState.bottomSheetState.isVisible
    ) {
      scaffoldState.bottomSheetState.hide()
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun rememberAppPlayerViewState(
  scaffoldState: BottomSheetScaffoldState,
  lastVisibleSheetValue: SheetValue = SheetValue.PartiallyExpanded,
  supportingPaneValue: PaneAdaptedValue = PaneAdaptedValue.Hidden,
): AppPlayerLayoutState {
  val state = remember {
    AppPlayerLayoutState(scaffoldState, lastVisibleSheetValue, supportingPaneValue)
  }

  LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
    if (scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
      state.lastVisibleSheetValue = scaffoldState.bottomSheetState.currentValue
    }
  }

  return state
}
