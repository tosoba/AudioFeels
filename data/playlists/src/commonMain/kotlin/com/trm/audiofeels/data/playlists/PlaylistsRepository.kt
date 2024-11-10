package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.core.network.HostFetcher
import com.trm.audiofeels.core.network.HostRetriever
import com.trm.audiofeels.core.network.hostInterceptor
import me.tatarka.inject.annotations.Inject

@Inject
class PlaylistsRepository(
  hostRetriever: HostRetriever,
  hostFetcher: HostFetcher,
  private val audiusEndpoints: AudiusEndpoints,
) {
  init {
    audiusEndpoints.addSendInterceptor(
      hostInterceptor(hostRetriever = hostRetriever, hostFetcher = hostFetcher)
    )
  }
}
