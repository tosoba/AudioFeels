package com.trm.audiofeels.ui.player.composable

import androidx.compose.runtime.Composable

@Composable expect fun playerRecordAudioPermissionHandler(onGranted: () -> Unit): () -> Unit
