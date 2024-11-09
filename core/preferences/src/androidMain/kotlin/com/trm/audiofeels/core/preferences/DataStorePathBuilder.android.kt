package com.trm.audiofeels.core.preferences

import android.content.Context

internal actual class DataStorePlatformPathBuilder(private val context: Context) :
  DataStorePathBuilder {
  override operator fun invoke(): String = context.filesDir.resolve(dataStoreFileName).absolutePath
}
