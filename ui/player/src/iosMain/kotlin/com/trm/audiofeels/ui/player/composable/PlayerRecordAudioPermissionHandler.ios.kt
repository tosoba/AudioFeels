package com.trm.audiofeels.ui.player.composable

import androidx.compose.runtime.Composable

@Composable
actual fun PlayerRecordAudioPermissionHandler(
  onDenied: () -> Unit,
  onDeniedPermanently: () -> Unit,
  onGranted: () -> Unit,
) {}
