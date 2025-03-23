package com.trm.audiofeels.api.audius

import com.trm.audiofeels.api.audius.model.PlaylistResponse
import com.trm.audiofeels.api.audius.model.PlaylistsResponse
import com.trm.audiofeels.core.network.configureDefault
import com.trm.audiofeels.core.network.host.HostFetcher
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.network.host.hostInterceptor
import com.trm.audiofeels.core.network.httpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments

class AudiusEndpoints(
  hostRetriever: HostRetriever,
  hostFetcher: HostFetcher,
  logLevel: LogLevel,
  engine: HttpClientEngine? = null,
  cacheStorage: CacheStorage? = null,
) {
  private val client: HttpClient =
    httpClient(engine = engine) {
        configureDefault(logLevel = logLevel, cacheStorage = cacheStorage, maxRetries = 2)
      }
      .apply { plugin(HttpSend).intercept(hostInterceptor(hostRetriever, hostFetcher)) }

  suspend fun getPlaylists(mood: String?): PlaylistsResponse =
    client
      .get {
        url {
          httpsPlaylistsEndpoint()
          appendPathSegments("top")
          parameter("type", "playlist")
          mood?.let { parameter("mood", it) }
        }
      }
      .body()

  suspend fun searchPlaylists(query: String): PlaylistsResponse =
    client
      .get {
        url {
          httpsPlaylistsEndpoint()
          appendPathSegments("search")
          parameter("query", query)
        }
      }
      .body()

  suspend fun getPlaylistById(id: String): PlaylistResponse =
    client
      .get {
        url {
          httpsPlaylistsEndpoint()
          appendPathSegments(id)
        }
      }
      .body()

  private fun URLBuilder.httpsPlaylistsEndpoint() {
    protocol = URLProtocol.HTTPS
    appendPathSegments(PLAYLISTS_PATH_SEGMENTS)
  }

  companion object {
    private val PLAYLISTS_PATH_SEGMENTS = listOf("v1", "full", "playlists")
  }
}
