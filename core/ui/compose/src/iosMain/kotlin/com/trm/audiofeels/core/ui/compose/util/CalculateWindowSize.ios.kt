package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun calculateWindowSize(): Size = LocalWindowInfo.current.containerSize.toSize()
