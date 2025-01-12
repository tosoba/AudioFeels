package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.ui.graphics.ImageBitmap
import coil3.Image

expect fun Image.toComposeImageBitmap(): ImageBitmap
