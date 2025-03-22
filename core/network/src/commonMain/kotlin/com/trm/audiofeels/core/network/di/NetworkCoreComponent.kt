package com.trm.audiofeels.core.network.di

import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import coil3.util.Logger
import coil3.util.Logger.Level
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.BuildInfo
import com.trm.audiofeels.core.base.util.PlatformContext
import com.trm.audiofeels.core.base.util.cachePath
import com.trm.audiofeels.core.network.host.HostValidator
import com.vipulasri.kachetor.KachetorStorage
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.cache.storage.CacheStorage
import me.tatarka.inject.annotations.Provides

interface NetworkCoreComponent : NetworkPlatformComponent {
  val imageLoader: ImageLoader
  val coilPlatformContext: coil3.PlatformContext

  @Provides @ApplicationScope fun hostValidator(): HostValidator = HostValidator()

  @Provides
  @ApplicationScope
  fun diskCache(platformContext: PlatformContext): CacheStorage =
    KachetorStorage(
      directoryPath = platformContext.cachePath.resolve("api_cache").toString(),
      maxSize = 10L * 1024L * 1024L,
    )

  @Provides
  @ApplicationScope
  fun imageLoader(
    platformContext: PlatformContext,
    coilPlatformContext: coil3.PlatformContext,
    buildInfo: BuildInfo,
  ): ImageLoader =
    ImageLoader.Builder(coilPlatformContext)
      .memoryCache {
        MemoryCache.Builder().maxSizePercent(coilPlatformContext, percent = 0.25).build()
      }
      .diskCache {
        DiskCache.Builder().directory(platformContext.cachePath.resolve("coil_cache")).build()
      }
      .crossfade(true)
      .run {
        if (buildInfo.debug)
          logger(
            object : Logger {
              override var minLevel: Level = Level.Debug

              override fun log(tag: String, level: Level, message: String?, throwable: Throwable?) {
                Napier.log(
                  priority = level.toLogLevel(),
                  tag = "Coil",
                  throwable = throwable,
                  message = message.orEmpty(),
                )
              }

              private fun Level.toLogLevel(): LogLevel =
                when (this) {
                  Level.Verbose -> LogLevel.VERBOSE
                  Level.Debug -> LogLevel.DEBUG
                  Level.Info -> LogLevel.INFO
                  Level.Warn -> LogLevel.WARNING
                  Level.Error -> LogLevel.ERROR
                }
            }
          )
        else this
      }
      .build()
}
