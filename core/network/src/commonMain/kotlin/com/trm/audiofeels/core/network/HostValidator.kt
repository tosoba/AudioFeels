package com.trm.audiofeels.core.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments

class HostValidator(private val client: HttpClient) {
  suspend fun isValid(host: String): Boolean =
    client.get(host) { url { appendPathSegments("v1") } }.status.value < 400
}
