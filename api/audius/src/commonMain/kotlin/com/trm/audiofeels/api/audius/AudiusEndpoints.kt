package com.trm.audiofeels.api.audius

import com.trm.audiofeels.api.audius.model.PlaylistResponse
import com.trm.audiofeels.api.audius.model.PlaylistsResponse
import com.trm.audiofeels.core.network.HostFetcher
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.core.network.hostInterceptor
import com.trm.audiofeels.core.network.httpClient
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.http.path

class AudiusEndpoints(private val client: HttpClient) {
  constructor(
    hostRetriever: HostRetriever,
    hostFetcher: HostFetcher,
    config: HttpClientConfig<*>.() -> Unit,
  ) : this(
    httpClient(config = config).apply {
      plugin(HttpSend).intercept(hostInterceptor(hostRetriever, hostFetcher))
    }
  )

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
