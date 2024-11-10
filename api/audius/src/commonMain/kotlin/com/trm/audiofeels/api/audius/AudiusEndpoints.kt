package com.trm.audiofeels.api.audius

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpSendInterceptor
import io.ktor.client.plugins.plugin

class AudiusEndpoints(private val client: HttpClient) {
  fun addSendInterceptor(interceptor: HttpSendInterceptor) {
    client.plugin(HttpSend).intercept(interceptor)
  }
}
