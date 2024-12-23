package com.trm.audiofeels.core.network.host

import io.ktor.client.plugins.HttpSendInterceptor
import io.ktor.http.HttpStatusCode

fun hostInterceptor(hostRetriever: HostRetriever, hostFetcher: HostFetcher): HttpSendInterceptor =
  { request ->
    val host = hostRetriever.retrieveHost()
    execute(request.also { it.url.host = host }).takeUnless {
      it.response.status == HttpStatusCode.NotFound
    } ?: execute(request.also { it.url.host = hostFetcher.fetchHost(host) })
  }

fun interface HostRetriever {
  suspend fun retrieveHost(): String
}

fun interface HostFetcher {
  suspend fun fetchHost(oldHost: String): String
}
