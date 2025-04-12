package com.trm.audiofeels.data.test

import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

abstract class PlaylistsFakeRepository : PlaylistsRepository {
  override suspend fun setNewCurrentPlaylist(playlist: Playlist, carryOn: Boolean) {
    throw UnsupportedOperationException()
  }

  override suspend fun updateCurrentPlaylist(
    id: String,
    currentTrackIndex: Int,
    currentTrackPositionMs: Long,
  ) {
    throw UnsupportedOperationException()
  }

  override suspend fun clearCurrentPlaylist() {
    throw UnsupportedOperationException()
  }

  override suspend fun toggleCurrentPlaylistFavourite() {
    throw UnsupportedOperationException()
  }

  override suspend fun getPlaylists(mood: Mood?): List<Playlist> {
    throw UnsupportedOperationException()
  }

  override suspend fun searchPlaylists(query: String): List<Playlist> {
    throw UnsupportedOperationException()
  }

  override suspend fun getPlaylistTracks(playlistId: String): List<Track> {
    throw UnsupportedOperationException()
  }

  override fun getCarryOnPlaylistsFlow(): Flow<List<CarryOnPlaylist>> {
    throw UnsupportedOperationException()
  }

  override fun getCurrentPlaylistFlow(): Flow<Playlist?> {
    throw UnsupportedOperationException()
  }

  override fun getCurrentPlaylistPlaybackFlow(): Flow<PlaylistPlayback?> {
    throw UnsupportedOperationException()
  }

  override fun getFavouritePlaylistsFlow(): Flow<List<Playlist>> {
    throw UnsupportedOperationException()
  }
}
