package com.trm.audiofeels.api.hosts

import com.trm.audiofeels.api.hosts.model.HostsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess

class HostsEndpoints(private val client: HttpClient) {
  suspend fun getHosts(): HostsResponse = client.get(URL).body<HostsResponse>()

  suspend fun pingHost(host: String): Boolean =
    client.get(host) { url { appendPathSegments("v1") } }.status.isSuccess()

  companion object {
    private const val URL = "https://api.audius.co"
  }
}
