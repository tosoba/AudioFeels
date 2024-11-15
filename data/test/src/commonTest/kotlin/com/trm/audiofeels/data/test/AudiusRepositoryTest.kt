package com.trm.audiofeels.data.test

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.api.hosts.model.HostsResponse
import com.trm.audiofeels.core.base.util.trimHttps
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

class AudiusRepositoryTest {
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
  fun `given no host stored locally - when call to getPlaylistsForMood - then first fetched host is pinged`() =
    runTest {
      val hostsClientEngine = hostsEngine()

      playlistsRepository(hostsEngine = hostsClientEngine).getPlaylistsForMood("Energizing")

      assertEquals(
        expected = hostAtIndex(0).trimHttps(),
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
            AudiusHostsInMemoryDataSource().apply { host = hostAtIndex(0).trimHttps() },
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
              AudiusHostsRepository.HOST_PREF_KEY to hostAtIndex(0).trimHttps()
            ),
        )
        .getPlaylistsForMood("Energizing")

      assertTrue(hostsClientEngine.requestHistory.isEmpty())
    }

  @Test
  fun `given host stored in preferences - when call to getPlaylistsForMood - then host is stored in memory`() =
    runTest {
      val hostsClientEngine = hostsEngine()
      val inMemoryDataSource = AudiusHostsInMemoryDataSource()

      playlistsRepository(
          hostsEngine = hostsClientEngine,
          inMemoryDataSource = inMemoryDataSource,
          dataStore =
            FakeDataStorePreferences(
              AudiusHostsRepository.HOST_PREF_KEY to hostAtIndex(0).trimHttps()
            ),
        )
        .getPlaylistsForMood("Energizing")

      assertEquals(expected = hostAtIndex(0).trimHttps(), actual = inMemoryDataSource.host)
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
        HOSTS_RESPONSE_JSON
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
      content = json,
      status = status,
      headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

  private fun contentNegotiationClientConfig(): HttpClientConfig<*>.() -> Unit = {
    install(ContentNegotiation) { json() }
  }

  companion object {
    private const val HOSTS_RESPONSE_JSON =
      """{"data":["https://audius-discovery-14.cultur3stake.com","https://audius-dp.amsterdam.creatorseed.com","https://blockchange-audius-discovery-04.bdnodes.net","https://discovery-au-02.audius.openplayer.org","https://audius-discovery-1.theblueprint.xyz","https://blockdaemon-audius-discovery-01.bdnodes.net","https://blockchange-audius-discovery-05.bdnodes.net","https://discoveryprovider2.audius.co","https://audius-metadata-1.figment.io","https://blockdaemon-audius-discovery-03.bdnodes.net","https://dn1.matterlightblooming.xyz","https://audius-discovery-2.altego.net","https://blockchange-audius-discovery-01.bdnodes.net","https://dn1.nodeoperator.io","https://discovery-us-01.audius.openplayer.org","https://audius-metadata-3.figment.io","https://dn1.monophonic.digital","https://blockchange-audius-discovery-02.bdnodes.net","https://blockdaemon-audius-discovery-04.bdnodes.net","https://audius-metadata-5.figment.io","https://audius-discovery-2.theblueprint.xyz","https://audius-discovery-11.cultur3stake.com","https://blockdaemon-audius-discovery-06.bdnodes.net","https://blockchange-audius-discovery-03.bdnodes.net","https://discoveryprovider.audius.co","https://dn2.monophonic.digital","https://discoveryprovider3.audius.co","https://audius-metadata-2.figment.io","https://audius-discovery-3.altego.net","https://dn-usa.audius.metadata.fyi","https://audius-discovery-8.cultur3stake.com","https://audius-discovery-17.cultur3stake.com","https://audius-discovery-18.cultur3stake.com","https://blockdaemon-audius-discovery-05.bdnodes.net","https://blockdaemon-audius-discovery-02.bdnodes.net","https://audius-discovery-1.altego.net","https://blockdaemon-audius-discovery-08.bdnodes.net","https://audius-nodes.com","https://dn-jpn.audius.metadata.fyi","https://audius-discovery-12.cultur3stake.com","https://audius-discovery-13.cultur3stake.com","https://audius-discovery-4.cultur3stake.com","https://audius-discovery-4.theblueprint.xyz","https://audius-metadata-4.figment.io","https://disc-lon01.audius.hashbeam.com","https://audius-dp.singapore.creatorseed.com","https://audius-discovery-3.theblueprint.xyz"]}"""

    private val hostsResponse = Json.decodeFromString<HostsResponse>(HOSTS_RESPONSE_JSON)

    fun hostAtIndex(index: Int): String = hostsResponse.hosts!![index]
  }
}
