package com.trm.audiofeels.core.ui.compose.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBarColorsWithGradient(): TopAppBarColors =
  TopAppBarDefaults.topAppBarColors().run {
    copy(containerColor = containerColor.copy(alpha = GRADIENT_BASE_ALPHA))
  }
