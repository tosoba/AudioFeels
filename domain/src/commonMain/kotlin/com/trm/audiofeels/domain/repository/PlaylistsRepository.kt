package com.trm.audiofeels.domain.repository

import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

interface PlaylistsRepository {
  fun getPlaylistsForMood(mood: String): List<Playlist>

  fun getPlaylistTracks(playlistId: String): List<Track>
}
