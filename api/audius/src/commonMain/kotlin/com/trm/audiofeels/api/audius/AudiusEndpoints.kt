package com.trm.audiofeels.api.audius

import com.trm.audiofeels.api.audius.model.PlaylistResponse
import com.trm.audiofeels.api.audius.model.PlaylistsResponse
import com.trm.audiofeels.core.network.host.HostFetcher
import com.trm.audiofeels.core.network.host.HostRetriever
import com.trm.audiofeels.core.network.configureDefault
import com.trm.audiofeels.core.network.host.hostInterceptor
import com.trm.audiofeels.core.network.httpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.http.path

class AudiusEndpoints(
  hostRetriever: HostRetriever,
  hostFetcher: HostFetcher,
  engine: HttpClientEngine? = null,
  cacheStorage: CacheStorage? = null,
) {
  private val client =
    httpClient(engine = engine) {
        configureDefault(logLevel = LogLevel.ALL, cacheStorage = cacheStorage, maxRetries = 2)
      }
      .apply { plugin(HttpSend).intercept(hostInterceptor(hostRetriever, hostFetcher)) }

  suspend fun getPlaylists(mood: String?): PlaylistsResponse =
    client
      .get {
        url {
          appendPathSegments("v1", "full", "playlists", "top")
          parameter("type", "playlist")
          mood?.let { parameter("mood", it) }
        }
      }
      .body()

  suspend fun getPlaylistById(id: String): PlaylistResponse =
    client.get { url { path("v1", "full", "playlists", id) } }.body()
}
