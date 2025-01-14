package com.trm.audiofeels.core.network.image

import coil3.Image
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.ImageRequest
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun ImageLoader.loadImageOrNull(platformContext: PlatformContext, url: String): Image? =
  suspendCancellableCoroutine { continuation ->
    enqueue(
      ImageRequest.Builder(platformContext)
        .data(url)
        .target(onSuccess = { continuation.resume(it) }, onError = { continuation.resume(null) })
        .build()
    )
  }
