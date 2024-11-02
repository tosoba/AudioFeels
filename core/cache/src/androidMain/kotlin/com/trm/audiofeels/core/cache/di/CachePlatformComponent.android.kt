package com.trm.audiofeels.core.cache.di

import android.app.Application
import com.trm.audio.feels.base.di.ApplicationScope
import com.trm.audiofeels.core.cache.disk.DiskCacheStorage
import io.ktor.client.plugins.cache.storage.CacheStorage
import me.tatarka.inject.annotations.Provides
import okio.FileSystem
import okio.Path.Companion.toOkioPath

actual interface CachePlatformComponent {
  @Provides
  @ApplicationScope
  fun cacheStorage(application: Application): CacheStorage =
    DiskCacheStorage(
      fileSystem = FileSystem.SYSTEM,
      directory = application.cacheDir.toOkioPath(),
      maxSize = CACHE_SIZE,
    )
}
