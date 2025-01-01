package com.trm.audiofeels.core.cache.di

import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.base.util.cachePath
import com.trm.audiofeels.core.cache.disk.DiskCacheStorage
import me.tatarka.inject.annotations.Provides
import okio.FileSystem
import okio.SYSTEM

interface CacheCoreComponent {
  @Provides
  @ApplicationScope
  fun cacheStorage(platformContext: PlatformContext): DiskCacheStorage =
    DiskCacheStorage(
      fileSystem = FileSystem.SYSTEM,
      directory = platformContext.cachePath,
      maxSize = CACHE_SIZE,
    )
}

private const val CACHE_SIZE = 1024L * 1024L * 10L
