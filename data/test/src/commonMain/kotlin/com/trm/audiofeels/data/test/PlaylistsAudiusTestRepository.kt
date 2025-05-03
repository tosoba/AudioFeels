package com.trm.audiofeels.data.test

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.data.database.createApplicationInMemoryDatabase
import com.trm.audiofeels.data.playlists.PlaylistsAudiusRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository

fun playlistsAudiusTestRepository(audiusEndpoints: AudiusEndpoints): PlaylistsRepository =
  PlaylistsAudiusRepository(
    audiusEndpoints = audiusEndpoints,
    playlistDao = createApplicationInMemoryDatabase(platformTestContext()).playlistDao(),
  )
