package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.api.audius.model.PlaylistsResponseItem
import com.trm.audiofeels.api.audius.model.TrackResponseItem
import com.trm.audiofeels.core.database.dao.PlaylistDao
import com.trm.audiofeels.core.database.model.PlaylistEntity
import com.trm.audiofeels.data.playlists.mapper.toCarryOn
import com.trm.audiofeels.data.playlists.mapper.toEntity
import com.trm.audiofeels.data.playlists.util.isValid
import com.trm.audiofeels.data.playlists.util.toPlaylist
import com.trm.audiofeels.data.playlists.util.toTrack
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class AudiusPlaylistsRepository(
  private val audiusEndpoints: AudiusEndpoints,
  private val playlistDao: PlaylistDao,
) : PlaylistsRepository {
  override suspend fun savePlaylist(playlist: Playlist) {
    playlistDao.insert(playlist.toEntity())
  }

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
