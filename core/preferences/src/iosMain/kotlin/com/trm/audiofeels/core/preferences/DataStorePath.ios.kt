package com.trm.audiofeels.core.preferences

import com.trm.audiofeels.core.base.util.PlatformContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSURL

actual val PlatformContext.dataStorePath: String
  get() {
    val documentDirectory: NSURL? =
      NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
      )
    return requireNotNull(documentDirectory).path + "/$dataStoreFileName"
  }
