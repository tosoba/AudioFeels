package com.trm.audiofeels.core.preferences

import com.trm.audiofeels.core.base.util.PlatformContext
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
internal actual val PlatformContext.dataStorePath: String
  get() =
    requireNotNull(
        NSFileManager.defaultManager.URLForDirectory(
          directory = NSDocumentDirectory,
          inDomain = NSUserDomainMask,
          appropriateForURL = null,
          create = false,
          error = null,
        )
      )
      .path + "/$dataStoreFileName"
