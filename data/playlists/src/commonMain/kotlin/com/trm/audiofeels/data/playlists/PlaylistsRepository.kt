package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.core.network.hostInterceptor
import com.trm.audiofeels.data.hosts.HostsRepository
import me.tatarka.inject.annotations.Inject

@Inject
class PlaylistsRepository(
  private val hostsRepository: HostsRepository,
  private val audiusEndpoints: AudiusEndpoints,
) {
  init {
    audiusEndpoints.addSendInterceptor(
      hostInterceptor(
        retrieveHost = hostsRepository::getHost,
        fetchNewHost = hostsRepository::fetchNewHost,
      )
    )
  }
}
