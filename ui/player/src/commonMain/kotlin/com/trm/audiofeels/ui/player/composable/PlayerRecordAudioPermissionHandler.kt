package com.trm.audiofeels.ui.player.composable

import androidx.compose.runtime.Composable

@Composable
expect fun PlayerRecordAudioPermissionHandler(
  onDenied: () -> Unit,
  onDeniedPermanently: () -> Unit,
  onGranted: () -> Unit,
)
