package com.trm.audiofeels.ui.player.composable

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun PlayerRecordAudioPermissionObserver(onGranted: () -> Unit, onDenied: () -> Unit) {
  val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
  LaunchedEffect(permissionState.status.isGranted) {
    if (permissionState.status.isGranted) onGranted() else onDenied()
  }
}
