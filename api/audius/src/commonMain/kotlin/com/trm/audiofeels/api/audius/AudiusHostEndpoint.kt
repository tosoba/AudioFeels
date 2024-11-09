package com.trm.audiofeels.api.audius

import com.trm.audiofeels.api.audius.model.HostsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class AudiusHostEndpoint(private val client: HttpClient) {
  suspend operator fun invoke(): HostsResponse = client.get(URL).body<HostsResponse>()

  companion object {
    private const val URL = "https://api.audius.co"
  }
}
