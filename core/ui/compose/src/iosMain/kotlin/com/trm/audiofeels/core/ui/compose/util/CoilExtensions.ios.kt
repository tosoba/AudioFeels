package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import coil3.Image
import coil3.toBitmap

internal actual fun Image.toComposeImageBitmap(): ImageBitmap = toBitmap().asComposeImageBitmap()
