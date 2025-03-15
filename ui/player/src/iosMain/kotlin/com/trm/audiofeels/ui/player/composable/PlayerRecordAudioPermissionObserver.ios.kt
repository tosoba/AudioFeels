package com.trm.audiofeels.ui.player.composable

import androidx.compose.runtime.Composable

@Composable
actual fun PlayerRecordAudioPermissionObserver(onGranted: () -> Unit, onDenied: () -> Unit) = Unit
