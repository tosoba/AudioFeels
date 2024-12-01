package com.trm.audiofeels

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AppContent() {
  MaterialTheme {
    val scope = rememberCoroutineScope()
    val scaffoldState =
      rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
      )
    BottomSheetScaffold(
      modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
      sheetContent = { Box(contentAlignment = Alignment.Center) { Text("TEST") } },
      scaffoldState = scaffoldState,
    ) {
      Column(
        modifier = Modifier.fillMaxSize().padding(it),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Button(
          onClick = {
            scope.launch {
              if (!scaffoldState.bottomSheetState.isVisible) scaffoldState.bottomSheetState.expand()
              else scaffoldState.bottomSheetState.hide()
            }
          }
        ) {
          Text("Click me!")
        }
      }
    }
  }
}
