package com.trm.audiofeels.data.test

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.core.network.hostInterceptor
import com.trm.audiofeels.data.hosts.AudiusHostsInMemoryDataSource
import com.trm.audiofeels.data.hosts.AudiusHostsRepository
import com.trm.audiofeels.data.playlists.AudiusPlaylistsRepository
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest

class AudiusPlaylistsRepositoryTest {
  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then hosts are fetched`() =
    runTest {
      val hostsClientEngine = hostsEngine()

      playlistsRepository(hostsEngine = hostsClientEngine).getPlaylistsForMood("Energizing")

      assertEquals(
        expected = HostsEndpoints.HOSTS_URL,
        actual = hostsClientEngine.requestHistory.first().url.toString(),
      )
    }

  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then fetched host is pinged`() =
    runTest {
      val hostsClientEngine = hostsEngine()

      playlistsRepository(hostsEngine = hostsClientEngine).getPlaylistsForMood("Energizing")

      assertEquals(
        expected = "audius-nodes.com",
        actual = hostsClientEngine.requestHistory.last().url.host,
      )
    }

  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then fetched host is stored in memory`() =
    runTest {
      val hostsClientEngine = hostsEngine()
      val inMemoryDataSource = AudiusHostsInMemoryDataSource()

      playlistsRepository(hostsEngine = hostsClientEngine, inMemoryDataSource = inMemoryDataSource)
        .getPlaylistsForMood("Energizing")

      assertEquals(
        expected = hostsClientEngine.requestHistory.last().url.host,
        actual = inMemoryDataSource.host,
      )
    }

  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then fetched host is stored in preferences`() =
    runTest {
      val hostsClientEngine = hostsEngine()
      val dataStore = FakeDataStorePreferences()

      playlistsRepository(hostsEngine = hostsClientEngine, dataStore = dataStore)
        .getPlaylistsForMood("Energizing")

      assertEquals(
        expected = hostsClientEngine.requestHistory.last().url.host,
        actual = dataStore.data.map { it[AudiusHostsRepository.HOST_PREF_KEY] }.first(),
      )
    }

  @Test
  fun `given host stored in memory - when call to getPlaylistsForMood - then hosts are not fetched`() =
    runTest {
      val hostsClientEngine = hostsEngine()

      playlistsRepository(
          hostsEngine = hostsClientEngine,
          inMemoryDataSource =
            AudiusHostsInMemoryDataSource().apply { host = "https://audius-nodes.com" },
        )
        .getPlaylistsForMood("Energizing")

      assertTrue(hostsClientEngine.requestHistory.isEmpty())
    }

  @Test
  fun `given host stored in preferences - when call to getPlaylistsForMood - then hosts are not fetched`() =
    runTest {
      val hostsClientEngine = hostsEngine()

      playlistsRepository(
          hostsEngine = hostsClientEngine,
          dataStore =
            FakeDataStorePreferences(
              AudiusHostsRepository.HOST_PREF_KEY to "https://audius-nodes.com"
            ),
        )
        .getPlaylistsForMood("Energizing")

      assertTrue(hostsClientEngine.requestHistory.isEmpty())
    }

  private fun playlistsRepository(
    hostsEngine: MockEngine,
    playlistsEngine: MockEngine = MockEngine { respondWithJson("""{"data":[]}""") },
    inMemoryDataSource: AudiusHostsInMemoryDataSource = AudiusHostsInMemoryDataSource(),
    dataStore: DataStore<Preferences> = FakeDataStorePreferences(),
  ): AudiusPlaylistsRepository {
    val hostsRepository =
      AudiusHostsRepository(
        inMemoryDataSource = inMemoryDataSource,
        dataStore = dataStore,
        endpoints =
          lazy { HostsEndpoints(HttpClient(hostsEngine, contentNegotiationClientConfig())) },
      )
    return AudiusPlaylistsRepository(
      audiusEndpoints =
        AudiusEndpoints(
          HttpClient(playlistsEngine, contentNegotiationClientConfig()).apply {
            plugin(HttpSend)
              .intercept(
                hostInterceptor(hostRetriever = hostsRepository, hostFetcher = hostsRepository)
              )
          }
        )
    )
  }

  private fun hostsEngine(): MockEngine = MockEngine { request ->
    respondWithJson(
      if (HostsEndpoints.HOSTS_URL == request.url.toString()) {
        """{"data":["https://audius-nodes.com"]}"""
      } else {
        "{}"
      }
    )
  }

  private fun MockRequestHandleScope.respondWithJson(
    json: String,
    status: HttpStatusCode = HttpStatusCode.OK,
  ): HttpResponseData =
    respond(
      content = ByteReadChannel(json),
      status = status,
      headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

  private fun contentNegotiationClientConfig(): HttpClientConfig<*>.() -> Unit = {
    install(ContentNegotiation) { json() }
  }
}
