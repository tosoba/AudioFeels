package com.trm.audiofeels.ui.player.composable

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.trm.audiofeels.core.base.util.getActivity
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.cancel
import com.trm.audiofeels.core.ui.resources.ok
import com.trm.audiofeels.core.ui.resources.permission_required
import com.trm.audiofeels.core.ui.resources.record_audio_permission_rationale
import com.trm.audiofeels.core.ui.resources.record_audio_permission_settings
import com.trm.audiofeels.core.ui.resources.settings
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun playerRecordAudioPermissionHandler(onGranted: () -> Unit): () -> Unit {
  val context = LocalContext.current

  var permissionDialogVisible by rememberSaveable { mutableStateOf(false) }
  var shouldShowRationale by rememberSaveable { mutableStateOf(false) }

  val requestPermissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
      if (isGranted) {
        onGranted()
      } else {
        shouldShowRationale =
          ActivityCompat.shouldShowRequestPermissionRationale(
            requireNotNull(context.getActivity()),
            Manifest.permission.RECORD_AUDIO,
          )
        permissionDialogVisible = true
      }
    }

  val settingsLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
      if (
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
          PackageManager.PERMISSION_GRANTED
      ) {
        onGranted()
      }
    }

  RecordAudioPermissionInfoDialog(
    visible = permissionDialogVisible,
    text =
      stringResource(
        if (shouldShowRationale) Res.string.record_audio_permission_rationale
        else Res.string.record_audio_permission_settings
      ),
    confirmText = stringResource(if (shouldShowRationale) Res.string.ok else Res.string.settings),
    onConfirmClick = {
      permissionDialogVisible = false
      if (shouldShowRationale) {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
      } else {
        settingsLauncher.launch(
          Intent(
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Settings.ACTION_APP_NOTIFICATION_SETTINGS
              } else {
                "android.settings.APP_NOTIFICATION_SETTINGS"
              }
            )
            .putExtra(
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Settings.EXTRA_APP_PACKAGE
              } else {
                "android.provider.extra.APP_PACKAGE"
              },
              context.packageName,
            )
        )
      }
    },
    onDismiss = { permissionDialogVisible = false },
  )

  return {
    if (
      ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
        PackageManager.PERMISSION_GRANTED
    ) {
      onGranted()
    } else {
      requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
  }
}

@Composable
private fun RecordAudioPermissionInfoDialog(
  visible: Boolean,
  text: String,
  confirmText: String,
  modifier: Modifier = Modifier,
  onConfirmClick: () -> Unit,
  onDismiss: () -> Unit,
) {
  AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
    AlertDialog(
      modifier = modifier,
      onDismissRequest = onDismiss,
      confirmButton = { TextButton(onClick = onConfirmClick) { Text(text = confirmText) } },
      dismissButton = {
        TextButton(onClick = onDismiss) { Text(text = stringResource(Res.string.cancel)) }
      },
      title = {
        Text(text = stringResource(Res.string.permission_required), textAlign = TextAlign.Center)
      },
      text = { Text(text = text) },
    )
  }
}
