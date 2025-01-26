package com.trm.audiofeels.core.preferences

import com.trm.audiofeels.core.base.util.PlatformContext

internal actual val PlatformContext.dataStorePath: String
  get() = filesDir.resolve(dataStoreFileName).absolutePath
