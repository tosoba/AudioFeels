package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.core.network.HostFetcher
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.core.network.hostInterceptor
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import me.tatarka.inject.annotations.Inject

@Inject
class AudiusPlaylistsRepository(
  hostRetriever: HostRetriever,
  hostFetcher: HostFetcher,
  private val audiusEndpoints: AudiusEndpoints,
) : PlaylistsRepository {
  init {
    audiusEndpoints.addSendInterceptor(
      hostInterceptor(hostRetriever = hostRetriever, hostFetcher = hostFetcher)
    )
  }

  override fun getPlaylistsForMood(mood: String): List<Playlist> {
    TODO("Not yet implemented")
  }

  override fun getPlaylistTracks(playlistId: String): List<Track> {
    TODO("Not yet implemented")
  }
}
