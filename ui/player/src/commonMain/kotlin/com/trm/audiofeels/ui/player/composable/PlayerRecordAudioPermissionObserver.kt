package com.trm.audiofeels.ui.player.composable

import androidx.compose.runtime.Composable

@Composable
expect fun PlayerRecordAudioPermissionObserver(onGranted: () -> Unit, onDenied: () -> Unit)
