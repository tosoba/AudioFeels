package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topAppBarSpacerHeight(): Dp =
  with(LocalDensity.current) { TopAppBarDefaults.windowInsets.getTop(this).toDp() } +
    TopAppBarDefaults.TopAppBarExpandedHeight
