package com.trm.audiofeels.data.test

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.api.hosts.HostsEndpoints
import com.trm.audiofeels.api.hosts.model.HostsResponse
import com.trm.audiofeels.core.base.util.trimHttps
import com.trm.audiofeels.core.network.HostValidator
import com.trm.audiofeels.core.network.hostInterceptor
import com.trm.audiofeels.core.preferences.get
import com.trm.audiofeels.core.preferences.hostPreferenceKey
import com.trm.audiofeels.data.hosts.AudiusHostsInMemoryDataSource
import com.trm.audiofeels.data.hosts.AudiusHostsRepository
import com.trm.audiofeels.data.playlists.AudiusPlaylistsRepository
import dev.mokkery.spy
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

class AudiusHostTest {
  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then hosts are fetched`() =
    runTest {
      val hostsEngine = defaultHostsEngine()

      playlistsRepository(hostsEngine = hostsEngine).getPlaylistsForMood("Energizing")

      hostsEngine.assertHostsWereFetchedFirst()
    }

  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then first fetched host is pinged`() =
    runTest {
      val hostsEngine = defaultHostsEngine()

      playlistsRepository(hostsEngine = hostsEngine).getPlaylistsForMood("Energizing")

      assertEquals(
        expected = hostAtIndex(0).trimHttps(),
        actual = hostsEngine.requestHistory.last().url.host,
      )
    }

  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then hosts are pinged until success`() =
    runTest {
      val firstSuccessHostIndex = 5

      val hostsEngine =
        defaultHostsEngine(
          (0..<firstSuccessHostIndex).associate {
            pingHostAtIndexUrl(it) to MockResponse(status = HttpStatusCode.NotFound)
          }
        )

      playlistsRepository(hostsEngine = hostsEngine).getPlaylistsForMood("Energizing")

      assertTrue(
        hostsEngine.requestHistory
          .map { it.url.host }
          .containsAll((0..firstSuccessHostIndex).map { hostAtIndex(it).trimHttps() })
      )
    }

  @Test
  fun `given no host stored locally - when multiple calls to getPlaylistsForMood - then hosts are fetched only once`() =
    runTest {
      val hostsEngine = defaultHostsEngine()
      val repository = playlistsRepository(hostsEngine = hostsEngine)

      List(5) { async { repository.getPlaylistsForMood("Energizing") } }.awaitAll()

      hostsEngine.assertHostsWereFetchedOnlyOnce()
    }

  @Test
  fun `given no host stored locally - when multiple calls to getPlaylistsForMood - then host is pinged only by first call`() =
    runTest {
      val hostsEngine = defaultHostsEngine()
      val repository = playlistsRepository(hostsEngine = hostsEngine)

      List(5) { async { repository.getPlaylistsForMood("Energizing") } }.awaitAll()

      assertEquals(
        expected = 1,
        actual =
          hostsEngine.requestHistory.map { it.url.host }.count { it == hostAtIndex(0).trimHttps() },
      )
    }

  @Test
  fun `given no host stored locally - when multiple calls to getPlaylistsForMood - then after host is stored it always retrieved from memory for subsequent calls`() =
    runTest {
      val hostsEngine =
        defaultHostsEngine(mapOf(pingHostAtIndexUrl(0) to MockResponse(delayMillis = 1_000L)))
      val dataStore = spy<DataStore<Preferences>>(FakeDataStorePreferences())
      val repository = playlistsRepository(hostsEngine = hostsEngine, dataStore = dataStore)

      List(5) { async { repository.getPlaylistsForMood("Energizing") } }.awaitAll()

      verify(mode = exactly(1)) { dataStore.data }
    }

  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then fetched host is stored in memory`() =
    runTest {
      val hostsEngine = defaultHostsEngine()
      val inMemoryDataSource = AudiusHostsInMemoryDataSource()

      playlistsRepository(hostsEngine = hostsEngine, inMemoryDataSource = inMemoryDataSource)
        .getPlaylistsForMood("Energizing")

      assertEquals(
        expected = hostsEngine.requestHistory.last().url.host,
        actual = inMemoryDataSource.host,
      )
    }

  @Test
  fun `given no host stored locally - when call to getPlaylistsForMood - then fetched host is stored in preferences`() =
    runTest {
      val hostsEngine = defaultHostsEngine()
      val dataStore = FakeDataStorePreferences()

      playlistsRepository(hostsEngine = hostsEngine, dataStore = dataStore)
        .getPlaylistsForMood("Energizing")

      assertEquals(
        expected = hostsEngine.requestHistory.last().url.host,
        actual = dataStore.get(hostPreferenceKey),
      )
    }

  @Test
  fun `given valid host stored in memory - when call to getPlaylistsForMood - then hosts are not fetched`() =
    runTest {
      val hostsEngine = defaultHostsEngine()

      playlistsRepository(
          hostsEngine = hostsEngine,
          inMemoryDataSource =
            AudiusHostsInMemoryDataSource().apply { host = hostAtIndex(0).trimHttps() },
        )
        .getPlaylistsForMood("Energizing")

      assertTrue(hostsEngine.requestHistory.isEmpty())
    }

  @Test
  fun `given valid host stored in preferences - when call to getPlaylistsForMood - then hosts are not fetched`() =
    runTest {
      val hostsEngine = defaultHostsEngine()

      playlistsRepository(
          hostsEngine = hostsEngine,
          dataStore = FakeDataStorePreferences(hostPreferenceKey to hostAtIndex(0).trimHttps()),
        )
        .getPlaylistsForMood("Energizing")

      assertTrue(hostsEngine.requestHistory.isEmpty())
    }

  @Test
  fun `given valid host stored in preferences - when call to getPlaylistsForMood - then host is stored in memory`() =
    runTest {
      val hostsEngine = defaultHostsEngine()
      val inMemoryDataSource = AudiusHostsInMemoryDataSource()

      playlistsRepository(
          hostsEngine = hostsEngine,
          inMemoryDataSource = inMemoryDataSource,
          dataStore = FakeDataStorePreferences(hostPreferenceKey to hostAtIndex(0).trimHttps()),
        )
        .getPlaylistsForMood("Energizing")

      assertEquals(expected = hostAtIndex(0).trimHttps(), actual = inMemoryDataSource.host)
    }

  @Test
  fun `given invalid host stored in preferences - when call to getPlaylistsForMood - then hosts are fetched`() =
    runTest {
      val invalidHost = "audius-invalid-host.com"
      val hostsEngine = defaultHostsEngine()

      playlistsRepository(
          hostsEngine = hostsEngine,
          playlistsEngine = defaultPlaylistsEngine(invalidHost),
          dataStore = FakeDataStorePreferences(hostPreferenceKey to invalidHost),
        )
        .getPlaylistsForMood("Energizing")

      hostsEngine.assertHostsWereFetchedFirst()
    }

  @Test
  fun `given invalid host stored in preferences - when call to getPlaylistsForMood - then fetched valid host is stored in memory`() =
    runTest {
      val invalidHost = "audius-invalid-host.com"
      val hostsEngine = defaultHostsEngine()
      val inMemoryDataSource = AudiusHostsInMemoryDataSource()

      playlistsRepository(
          hostsEngine = hostsEngine,
          playlistsEngine = defaultPlaylistsEngine(invalidHost),
          inMemoryDataSource = inMemoryDataSource,
          dataStore = FakeDataStorePreferences(hostPreferenceKey to invalidHost),
        )
        .getPlaylistsForMood("Energizing")

      assertEquals(expected = hostAtIndex(0).trimHttps(), actual = inMemoryDataSource.host)
    }

  @Test
  fun `given invalid host stored in preferences - when call to getPlaylistsForMood - then fetched valid host is stored in preferences`() =
    runTest {
      val invalidHost = "audius-invalid-host.com"
      val hostsEngine = defaultHostsEngine()
      val dataStore = FakeDataStorePreferences(hostPreferenceKey to invalidHost)
      val repository =
        playlistsRepository(
          hostsEngine = hostsEngine,
          playlistsEngine = defaultPlaylistsEngine(invalidHost),
          dataStore = dataStore,
        )

      List(5) { async { repository.getPlaylistsForMood("Energizing") } }.awaitAll()

      assertEquals(expected = hostAtIndex(0).trimHttps(), actual = dataStore.get(hostPreferenceKey))
    }

  @Test
  fun `given invalid host stored in preferences - when multiple calls to getPlaylistsForMood - then hosts are fetched only once`() =
    runTest {
      val invalidHost = "audius-invalid-host.com"
      val hostsEngine = defaultHostsEngine()
      val repository =
        playlistsRepository(
          hostsEngine = hostsEngine,
          playlistsEngine = defaultPlaylistsEngine(invalidHost),
          dataStore = FakeDataStorePreferences(hostPreferenceKey to invalidHost),
        )

      List(5) { async { repository.getPlaylistsForMood("Energizing") } }.awaitAll()

      hostsEngine.assertHostsWereFetchedOnlyOnce()
    }

  @Test
  fun `given invalid host stored in preferences - when multiple calls to getPlaylistsForMood - then host is pinged only by first call`() =
    runTest {
      val invalidHost = "audius-invalid-host.com"
      val hostsEngine = defaultHostsEngine()
      val repository =
        playlistsRepository(
          hostsEngine = hostsEngine,
          playlistsEngine = defaultPlaylistsEngine(invalidHost),
          dataStore = FakeDataStorePreferences(hostPreferenceKey to invalidHost),
        )

      List(5) { async { repository.getPlaylistsForMood("Energizing") } }.awaitAll()

      assertEquals(
        expected = 1,
        actual =
          hostsEngine.requestHistory.map { it.url.host }.count { it == hostAtIndex(0).trimHttps() },
      )
    }

  private fun playlistsRepository(
    hostsEngine: MockEngine,
    playlistsEngine: MockEngine = defaultPlaylistsEngine(),
    inMemoryDataSource: AudiusHostsInMemoryDataSource = AudiusHostsInMemoryDataSource(),
    dataStore: DataStore<Preferences> = FakeDataStorePreferences(),
  ): AudiusPlaylistsRepository {
    val hostsRepository =
      AudiusHostsRepository(
        inMemoryDataSource = inMemoryDataSource,
        dataStore = dataStore,
        endpoints =
          lazy { HostsEndpoints(HttpClient(hostsEngine, contentNegotiationClientConfig())) },
        validator =
          lazy { HostValidator(HttpClient(hostsEngine, contentNegotiationClientConfig())) },
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

  private fun defaultHostsEngine(urlResponses: Map<String, MockResponse> = emptyMap()): MockEngine =
    MockEngine { request ->
      val url = request.url.toString()
      urlResponses[url]?.let { (json, status, delayMillis) ->
        delayMillis.takeIf { it > 0 }?.let { delay(it) }
        respondWithJson(json, status)
      }
        ?: respondWithJson(
          if (url == HostsEndpoints.HOSTS_URL) {
            HOSTS_RESPONSE_JSON
          } else {
            "{}"
          }
        )
    }

  private fun defaultPlaylistsEngine(vararg invalidHosts: String): MockEngine =
    MockEngine { request ->
      if (request.url.host in invalidHosts) respondWithJson(status = HttpStatusCode.NotFound)
      else respondWithJson("""{"data":[]}""")
    }

  private data class MockResponse(
    val json: String = "{}",
    val status: HttpStatusCode = HttpStatusCode.OK,
    val delayMillis: Long = 0L,
  )

  private fun MockRequestHandleScope.respondWithJson(
    json: String = "{}",
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

  private fun MockEngine.assertHostsWereFetchedFirst() {
    assertEquals(
      expected = HostsEndpoints.HOSTS_URL,
      actual = requestHistory.first().url.toString(),
    )
  }

  private fun MockEngine.assertHostsWereFetchedOnlyOnce() {
    assertEquals(
      expected = 1,
      actual = requestHistory.count { it.url.toString() == HostsEndpoints.HOSTS_URL },
    )
  }

  companion object {
    private const val HOSTS_RESPONSE_JSON =
      """{"data":["https://audius-discovery-14.cultur3stake.com","https://audius-dp.amsterdam.creatorseed.com","https://blockchange-audius-discovery-04.bdnodes.net","https://discovery-au-02.audius.openplayer.org","https://audius-discovery-1.theblueprint.xyz","https://blockdaemon-audius-discovery-01.bdnodes.net","https://blockchange-audius-discovery-05.bdnodes.net","https://discoveryprovider2.audius.co","https://audius-metadata-1.figment.io","https://blockdaemon-audius-discovery-03.bdnodes.net","https://dn1.matterlightblooming.xyz","https://audius-discovery-2.altego.net","https://blockchange-audius-discovery-01.bdnodes.net","https://dn1.nodeoperator.io","https://discovery-us-01.audius.openplayer.org","https://audius-metadata-3.figment.io","https://dn1.monophonic.digital","https://blockchange-audius-discovery-02.bdnodes.net","https://blockdaemon-audius-discovery-04.bdnodes.net","https://audius-metadata-5.figment.io","https://audius-discovery-2.theblueprint.xyz","https://audius-discovery-11.cultur3stake.com","https://blockdaemon-audius-discovery-06.bdnodes.net","https://blockchange-audius-discovery-03.bdnodes.net","https://discoveryprovider.audius.co","https://dn2.monophonic.digital","https://discoveryprovider3.audius.co","https://audius-metadata-2.figment.io","https://audius-discovery-3.altego.net","https://dn-usa.audius.metadata.fyi","https://audius-discovery-8.cultur3stake.com","https://audius-discovery-17.cultur3stake.com","https://audius-discovery-18.cultur3stake.com","https://blockdaemon-audius-discovery-05.bdnodes.net","https://blockdaemon-audius-discovery-02.bdnodes.net","https://audius-discovery-1.altego.net","https://blockdaemon-audius-discovery-08.bdnodes.net","https://audius-nodes.com","https://dn-jpn.audius.metadata.fyi","https://audius-discovery-12.cultur3stake.com","https://audius-discovery-13.cultur3stake.com","https://audius-discovery-4.cultur3stake.com","https://audius-discovery-4.theblueprint.xyz","https://audius-metadata-4.figment.io","https://disc-lon01.audius.hashbeam.com","https://audius-dp.singapore.creatorseed.com","https://audius-discovery-3.theblueprint.xyz"]}"""

    private val hostsResponse = Json.decodeFromString<HostsResponse>(HOSTS_RESPONSE_JSON)

    private fun hostAtIndex(index: Int): String = hostsResponse.hosts!![index]

    private fun pingHostAtIndexUrl(index: Int): String = "${hostAtIndex(index)}/v1"
  }
}
