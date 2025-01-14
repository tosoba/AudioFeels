package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.ui.graphics.ImageBitmap
import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import com.trm.audiofeels.core.network.image.loadImageOrNull

internal expect fun Image.toComposeImageBitmap(): ImageBitmap

suspend fun ImageLoader.loadImageBitmapOrNull(
  url: String,
  platformContext: PlatformContext,
): ImageBitmap? = loadImageOrNull(platformContext, url)?.toComposeImageBitmap()
