package com.trm.audiofeels.core.network.di

import com.trm.audiofeels.core.network.HostValidator
import com.trm.audiofeels.core.network.configureDefault
import com.trm.audiofeels.core.network.httpClient
import io.ktor.client.plugins.logging.LogLevel
import me.tatarka.inject.annotations.Provides

interface NetworkCoreComponent {
  @Provides
  fun hostValidator(): HostValidator =
    HostValidator(
      httpClient {
        configureDefault(logLevel = LogLevel.ALL, expectSuccess = false, maxRetries = 2)
      }
    )
}
