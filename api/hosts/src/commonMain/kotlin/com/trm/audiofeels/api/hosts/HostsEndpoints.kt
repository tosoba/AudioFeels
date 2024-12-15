package com.trm.audiofeels.api.hosts

import com.trm.audiofeels.api.hosts.model.HostsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class HostsEndpoints(private val client: HttpClient) {
  suspend fun getHosts(): HostsResponse = client.get(HOSTS_URL).body<HostsResponse>()

  companion object {
    const val HOSTS_URL = "https://api.audius.co"
  }
}
