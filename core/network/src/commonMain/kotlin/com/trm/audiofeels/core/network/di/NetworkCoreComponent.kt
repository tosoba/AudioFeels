package com.trm.audiofeels.core.network.di

import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.base.util.cachePath
import com.trm.audiofeels.core.network.host.HostValidator
import com.trm.audiofeels.core.network.monitor.NetworkMonitor
import com.trm.audiofeels.core.network.monitor.NetworkPlatformMonitor
import me.tatarka.inject.annotations.Provides

interface NetworkCoreComponent : NetworkPlatformComponent {
  val imageLoader: ImageLoader

  @Provides fun hostValidator(): HostValidator = HostValidator()

  @Provides
  @ApplicationScope
  fun networkMonitor(platformContext: PlatformContext): NetworkMonitor =
    NetworkPlatformMonitor(platformContext = platformContext)

  @Provides
  fun newImageLoader(
    platformContext: PlatformContext,
    coilPlatformContext: coil3.PlatformContext,
  ): ImageLoader =
    ImageLoader.Builder(coilPlatformContext)
      .memoryCache {
        MemoryCache.Builder().maxSizePercent(coilPlatformContext, percent = 0.25).build()
      }
      .diskCache {
        DiskCache.Builder().directory(platformContext.cachePath.resolve("coil_cache")).build()
      }
      .build()
}
