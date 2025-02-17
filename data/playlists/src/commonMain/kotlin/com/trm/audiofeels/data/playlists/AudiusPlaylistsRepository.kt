package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.api.audius.model.PlaylistsResponseItem
import com.trm.audiofeels.api.audius.model.TrackResponseItem
import com.trm.audiofeels.core.database.dao.PlaylistDao
import com.trm.audiofeels.core.database.model.PlaylistEntity
import com.trm.audiofeels.data.playlists.mapper.toCarryOn
import com.trm.audiofeels.data.playlists.mapper.toCurrentPlaylistEntity
import com.trm.audiofeels.data.playlists.mapper.toPlaylistPlayback
import com.trm.audiofeels.data.playlists.util.isValid
import com.trm.audiofeels.data.playlists.util.toPlaylist
import com.trm.audiofeels.data.playlists.util.toTrack
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

@Inject
class AudiusPlaylistsRepository(
  private val audiusEndpoints: AudiusEndpoints,
  private val playlistDao: PlaylistDao,
) : PlaylistsRepository {
  override suspend fun setNewCurrentPlaylist(playlist: Playlist, carryOn: Boolean) {
    playlistDao.setNewCurrentPlaylist(playlist.toCurrentPlaylistEntity(), carryOn)
  }

  override suspend fun updateCurrentPlaylist(playlistPlayback: PlaylistPlayback) {
    playlistDao.upsert(playlistPlayback.toCurrentPlaylistEntity())
  }

  override suspend fun clearCurrentPlaylist() {
    playlistDao.clearCurrentPlaylist(Clock.System.now())
  }

  override fun getCurrentPlaylistPlaybackFlow(): Flow<PlaylistPlayback?> =
    playlistDao.selectCurrentPlaylist().map { it?.toPlaylistPlayback() }

  override suspend fun getPlaylists(mood: String?): List<Playlist> =
    audiusEndpoints
      .getPlaylists(mood)
      .items
      ?.filter(PlaylistsResponseItem::isValid)
      ?.map(PlaylistsResponseItem::toPlaylist)
      .orEmpty()

  override suspend fun getPlaylistTracks(playlistId: String): List<Track> =
    audiusEndpoints
      .getPlaylistById(playlistId)
      .items
      ?.firstOrNull()
      ?.tracks
      ?.filter(TrackResponseItem::isValid)
      ?.map(TrackResponseItem::toTrack)
      .orEmpty()

  override fun getCarryOnPlaylistsFlow(): Flow<List<CarryOnPlaylist>> =
    playlistDao.selectAllOrderByLastPlayed().map { it.map(PlaylistEntity::toCarryOn) }
}
