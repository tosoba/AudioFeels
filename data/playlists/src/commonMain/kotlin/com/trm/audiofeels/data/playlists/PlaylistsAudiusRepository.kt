package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.data.database.dao.PlaylistDao
import com.trm.audiofeels.data.database.model.PlaylistEntity
import com.trm.audiofeels.data.playlists.mapper.toCarryOn
import com.trm.audiofeels.data.playlists.mapper.toCurrentPlaylistEntity
import com.trm.audiofeels.data.playlists.mapper.toPlaylist
import com.trm.audiofeels.data.playlists.mapper.toPlaylistPlayback
import com.trm.audiofeels.data.playlists.util.toPlaylists
import com.trm.audiofeels.data.playlists.util.toTracks
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

@Inject
class PlaylistsAudiusRepository(
  private val audiusEndpoints: AudiusEndpoints,
  private val playlistDao: PlaylistDao,
) : PlaylistsRepository {
  override suspend fun setNewCurrentPlaylist(playlist: Playlist, carryOn: Boolean) {
    playlistDao.setNewCurrentPlaylist(playlist.toCurrentPlaylistEntity(), carryOn)
  }

  override suspend fun updateCurrentPlaylist(
    id: String,
    currentTrackIndex: Int,
    currentTrackPositionMs: Long,
  ) {
    playlistDao.updateCurrentPlaylist(
      id = id,
      currentTrackIndex = currentTrackIndex,
      currentTrackPositionMs = currentTrackPositionMs,
    )
  }

  override suspend fun clearCurrentPlaylist() {
    playlistDao.clearCurrentPlaylist(Clock.System.now())
  }

  override suspend fun toggleCurrentPlaylistFavourite() {
    playlistDao.toggleCurrentPlaylistFavourite()
  }

  override fun getCurrentPlaylistFlow(): Flow<Playlist?> =
    playlistDao.selectCurrentPlaylist().map { it?.toPlaylist() }

  override fun getCurrentPlaylistPlaybackFlow(): Flow<PlaylistPlayback?> =
    playlistDao.selectCurrentPlaylist().map { it?.toPlaylistPlayback() }

  override suspend fun getPlaylists(mood: Mood?): List<Playlist> =
    audiusEndpoints.getPlaylists(mood?.name).toPlaylists()

  override suspend fun searchPlaylists(query: String): List<Playlist> =
    audiusEndpoints.searchPlaylists(query).toPlaylists()

  override suspend fun getPlaylistTracks(playlistId: String): List<Track> =
    audiusEndpoints.getPlaylistById(playlistId).toTracks()

  override fun getCarryOnPlaylistsFlow(): Flow<List<CarryOnPlaylist>> =
    playlistDao.selectAllOrderByLastPlayed().map { it.map(PlaylistEntity::toCarryOn) }

  override fun getFavouritePlaylistsFlow(): Flow<List<Playlist>> =
    playlistDao.selectFavouritePlaylists().map { it.map(PlaylistEntity::toPlaylist) }
}
