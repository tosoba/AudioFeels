package com.trm.audiofeels.domain.repository

import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

interface PlaylistsRepository {
  suspend fun getPlaylists(mood: Mood?): List<Playlist>

  suspend fun searchPlaylists(query: String): List<Playlist>

  suspend fun getPlaylistTracks(playlistId: String): List<Track>
}
