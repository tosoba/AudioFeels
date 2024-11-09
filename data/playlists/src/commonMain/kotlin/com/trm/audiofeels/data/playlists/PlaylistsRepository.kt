package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.data.hosts.HostsRepository
import me.tatarka.inject.annotations.Inject

@Inject
class PlaylistsRepository(
  private val hostsRepository: Lazy<HostsRepository>,
  private val audiusEndpoints: Lazy<AudiusEndpoints>,
) {}
