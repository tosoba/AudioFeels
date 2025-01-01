package com.trm.audiofeels.core.network.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import coil3.util.Logger.Level
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
      .crossfade(true)
      .logger(Logger.asCoilLogger())
      .build()
}

private fun Logger.asCoilLogger(): coil3.util.Logger =
  object : coil3.util.Logger {
    override var minLevel: Level = Level.Debug

    override fun log(tag: String, level: Level, message: String?, throwable: Throwable?) {
      this@asCoilLogger.log(level.toSeverity(), "Coil", throwable, message.orEmpty())
    }
  }

private fun Level.toSeverity(): Severity =
  when (this) {
    Level.Verbose -> Severity.Verbose
    Level.Debug -> Severity.Debug
    Level.Info -> Severity.Info
    Level.Warn -> Severity.Warn
    Level.Error -> Severity.Error
  }
