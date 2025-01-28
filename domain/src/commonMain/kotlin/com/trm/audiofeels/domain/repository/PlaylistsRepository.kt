package com.trm.audiofeels.domain.repository

import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
  suspend fun savePlaylist(playlist: Playlist)

  suspend fun getPlaylists(mood: String?): List<Playlist>

  suspend fun getPlaylistTracks(playlistId: String): List<Track>

  fun getCarryOnPlaylistsFlow(): Flow<List<CarryOnPlaylist>>
}
