package com.trm.audiofeels.api.audius

import com.trm.audiofeels.api.audius.model.PlaylistResponse
import com.trm.audiofeels.api.audius.model.PlaylistsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpSendInterceptor
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.appendPathSegments
import io.ktor.http.path

class AudiusEndpoints(private val client: HttpClient) {
  suspend fun getPlaylists(mood: String): PlaylistsResponse =
    client
      .get {
        url {
          appendPathSegments("v1", "full", "playlists", "top")
          parameter("type", "playlist")
          parameter("mood", mood)
        }
      }
      .body()

  suspend fun getPlaylistById(id: String): PlaylistResponse =
    client.get { url { path("v1", "full", "playlists", id) } }.body()

  fun addSendInterceptor(interceptor: HttpSendInterceptor) {
    client.plugin(HttpSend).intercept(interceptor)
  }
}
