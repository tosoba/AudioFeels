package com.trm.audiofeels.core.cache.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.cache.disk.DiskCacheStorage
import me.tatarka.inject.annotations.Provides
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual interface CachePlatformComponent {
  @Provides
  @ApplicationScope
  fun cacheStorage(): DiskCacheStorage =
    DiskCacheStorage(
      fileSystem = FileSystem.SYSTEM,
      directory =
        NSSearchPathForDirectoriesInDomains(
            directory = NSCachesDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
          )
          .first()
          .toString()
          .toPath(),
      maxSize = CACHE_SIZE,
    )
}
