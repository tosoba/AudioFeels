package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun currentWindowHeightClass(): WindowHeightSizeClass =
  WindowSizeClass.calculateFromSize(currentWindowDpSize()).heightSizeClass

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun currentWindowWidthClass(): WindowWidthSizeClass =
    WindowSizeClass.calculateFromSize(currentWindowDpSize()).widthSizeClass
