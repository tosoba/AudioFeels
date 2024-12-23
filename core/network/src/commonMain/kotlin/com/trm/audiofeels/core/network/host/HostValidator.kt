package com.trm.audiofeels.core.network.host

import com.trm.audiofeels.core.network.configureDefault
import com.trm.audiofeels.core.network.httpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments

class HostValidator(engine: HttpClientEngine? = null) {
  private val client =
    httpClient(engine) {
      configureDefault(
        logLevel = LogLevel.ALL,
        expectSuccess = false,
        followRedirects = false,
        maxRetries = 2,
      )
    }

  suspend fun isValid(host: String): Boolean =
    client.get(host) { url { appendPathSegments("v1") } }.status.value < 400
}
