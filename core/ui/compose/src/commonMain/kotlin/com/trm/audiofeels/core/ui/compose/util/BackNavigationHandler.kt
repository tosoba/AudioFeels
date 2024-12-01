package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.runtime.Composable

@Composable expect fun BackNavigationHandler(enabled: Boolean, onBack: () -> Unit)
