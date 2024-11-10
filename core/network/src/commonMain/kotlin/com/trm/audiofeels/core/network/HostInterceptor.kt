package com.trm.audiofeels.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpStatusCode

fun HttpClient.pluginHostInterceptor(
  retrieveHost: suspend () -> String,
  fetchNewHost: suspend (String) -> String,
): HttpClient {
  plugin(HttpSend).intercept { request ->
    val host = retrieveHost()
    val originalCall = execute(request.also { it.url.host = host })
    if (originalCall.response.status == HttpStatusCode.NotFound) {
      execute(request.also { it.url.host = fetchNewHost(host) })
    } else {
      originalCall
    }
  }
  return this
}
