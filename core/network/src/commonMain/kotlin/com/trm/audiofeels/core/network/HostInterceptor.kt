package com.trm.audiofeels.core.network

import io.ktor.client.plugins.HttpSendInterceptor
import io.ktor.http.HttpStatusCode

fun hostInterceptor(
  retrieveHost: suspend () -> String,
  fetchNewHost: suspend (String) -> String,
): HttpSendInterceptor = { request ->
  val host = retrieveHost()
  val originalCall = execute(request.also { it.url.host = host })
  if (originalCall.response.status == HttpStatusCode.NotFound) {
    execute(request.also { it.url.host = fetchNewHost(host) })
  } else {
    originalCall
  }
}
