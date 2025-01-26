package com.trm.audiofeels.core.preferences

import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.base.util.documentDirectoryPath

internal actual val PlatformContext.dataStorePath: String
  get() = "$documentDirectoryPath/$dataStoreFileName"
