package com.trm.audiofeels.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.http.isSuccess

fun HttpClient.useHostInterceptor(
  retrieveHost: suspend () -> String,
  onResponseSuccess: suspend (String) -> Unit,
): HttpClient {
  plugin(HttpSend).intercept { request ->
    val host = retrieveHost()
    execute(requestBuilder = request.apply { url.host = host }).also {
      if (it.response.status.isSuccess()) {
        onResponseSuccess(host)
      }
    }
  }
  return this
}
