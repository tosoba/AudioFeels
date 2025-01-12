package com.trm.audiofeels.core.ui.compose.util

import androidx.compose.ui.graphics.ImageBitmap
import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.ImageRequest
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

internal expect fun Image.toComposeImageBitmap(): ImageBitmap

suspend fun ImageLoader.loadImageBitmapOrNull(
  url: String,
  platformContext: PlatformContext,
): ImageBitmap? = suspendCancellableCoroutine { continuation ->
  enqueue(
    ImageRequest.Builder(platformContext)
      .data(url)
      .target(
        onSuccess = { continuation.resume(it.toComposeImageBitmap()) },
        onError = { continuation.resume(null) },
      )
      .build()
  )
}
