package com.trm.audiofeels.data.test

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.core.network.hostInterceptor
import com.trm.audiofeels.data.hosts.AudiusHostsInMemoryDataSource
import com.trm.audiofeels.data.hosts.AudiusHostsRepository
import com.trm.audiofeels.data.playlists.AudiusPlaylistsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HostInterceptorTest {
  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then hosts are fetched`() =
    runTest {
      val hostsClientMockEngine = hostsClientMockEngine()

      val hostsRepository =
        AudiusHostsRepository(
          inMemoryDataSource = AudiusHostsInMemoryDataSource(),
          dataStore = FakeDataStorePreferences(),
          endpoints =
            lazy {
              HostsEndpoints(
                HttpClient(hostsClientMockEngine) { install(ContentNegotiation) { json() } }
              )
            },
        )

      AudiusPlaylistsRepository(
          audiusEndpoints =
            AudiusEndpoints(
              HttpClient(
                  MockEngine {
                    respond(
                      content = ByteReadChannel("""{"data":[]}"""),
                      status = HttpStatusCode.OK,
                      headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                  }
                ) {
                  install(ContentNegotiation) { json() }
                }
                .apply {
                  plugin(HttpSend)
                    .intercept(
                      hostInterceptor(
                        hostRetriever = hostsRepository,
                        hostFetcher = hostsRepository,
                      )
                    )
                }
            )
        )
        .getPlaylistsForMood("Energizing")

      assertEquals(
        hostsClientMockEngine.requestHistory.first().url.toString(),
        HostsEndpoints.HOSTS_URL,
      )
    }

  private fun hostsClientMockEngine() = MockEngine { request ->
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
}
