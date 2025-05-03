package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.data.playlists.util.toPlaylists
import com.trm.audiofeels.data.playlists.util.toTracks
import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import me.tatarka.inject.annotations.Inject

@Inject
class PlaylistsAudiusRepository(private val audiusEndpoints: AudiusEndpoints) :
  PlaylistsRepository {
  override suspend fun getPlaylists(mood: Mood?): List<Playlist> =
    audiusEndpoints.getPlaylists(mood?.name).toPlaylists()

  override suspend fun searchPlaylists(query: String): List<Playlist> =
    audiusEndpoints.searchPlaylists(query).toPlaylists()

  override suspend fun getPlaylistTracks(playlistId: String): List<Track> =
    audiusEndpoints.getPlaylistById(playlistId).toTracks()
}
