package com.trm.audiofeels.ui.player

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PlayerPage(modifier: Modifier = Modifier, onCancelPlaybackClick: () -> Unit) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Button(onClick = onCancelPlaybackClick) { Text("Cancel") }
  }
}
