package com.trm.audiofeels.core.preferences

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSURL

internal actual class DataStorePlatformPathBuilder : DataStorePathBuilder {
  override operator fun invoke(): String {
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
}
