package com.trm.audiofeels.data.test

import com.trm.audiofeels.api.hosts.HostsEndpoints
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class HostInterceptorTest {
  @Test
  fun t() = runTest {
    val response =
      HostsEndpoints(
          HttpClient(
            MockEngine { request ->
              if (HostsEndpoints.HOSTS_URL == request.url.toString()) {
                respond(
                  content = ByteReadChannel("""{"data":["https://audius-nodes.com"]}"""),
                  status = HttpStatusCode.OK,
                  headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
              } else {
                respond(
                  content = ByteReadChannel("""{}"""),
                  status = HttpStatusCode.OK,
                  headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
              }
            }
          ) {
            install(ContentNegotiation) { json() }
          }
        )
        .getHosts()
    println(response)
  }
}
