package com.trm.audiofeels.core.cache.di

import android.app.Application
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.cache.disk.DiskCacheStorage
import me.tatarka.inject.annotations.Provides
import okio.FileSystem
import okio.Path.Companion.toOkioPath

actual interface CachePlatformComponent {
  @Provides
  @ApplicationScope
  fun cacheStorage(application: Application): DiskCacheStorage =
    DiskCacheStorage(
      fileSystem = FileSystem.SYSTEM,
      directory = application.cacheDir.toOkioPath(),
      maxSize = CACHE_SIZE,
    )
}
