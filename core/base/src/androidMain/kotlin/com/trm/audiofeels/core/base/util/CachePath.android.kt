package com.trm.audiofeels.core.base.util

import okio.Path
import okio.Path.Companion.toOkioPath

actual val PlatformContext.cachePath: Path
  get() = cacheDir.toOkioPath()
