package com.trm.audiofeels.domain.repository

import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
  suspend fun setNewCurrentPlaylist(playlist: Playlist, carryOn: Boolean)

  suspend fun updateCurrentPlaylist(
    id: String,
    currentTrackIndex: Int,
    currentTrackPositionMs: Long,
  )

  suspend fun clearCurrentPlaylist()

  suspend fun toggleCurrentPlaylistFavourite()

  suspend fun getPlaylists(mood: String?): List<Playlist>

  suspend fun searchPlaylists(query: String): List<Playlist>

  suspend fun getPlaylistTracks(playlistId: String): List<Track>

  fun getCarryOnPlaylistsFlow(): Flow<List<CarryOnPlaylist>>

  fun getCurrentPlaylistFlow(): Flow<Playlist?>

  fun getCurrentPlaylistPlaybackFlow(): Flow<PlaylistPlayback?>

  fun getFavouritePlaylistsFlow(): Flow<List<Playlist>>
}
