package com.trm.audiofeels.api.hosts

import com.trm.audiofeels.api.hosts.model.HostsResponse
import com.trm.audiofeels.core.network.configureDefault
import com.trm.audiofeels.core.network.httpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.request.get

class HostsEndpoints(logLevel: LogLevel, engine: HttpClientEngine? = null) {
  private val client = httpClient(engine) { configureDefault(logLevel = logLevel, maxRetries = 2) }

  suspend fun getHosts(): HostsResponse = client.get(HOSTS_URL).body<HostsResponse>()

  companion object {
    const val HOSTS_URL = "https://api.audius.co"
  }
}
