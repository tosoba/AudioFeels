package com.trm.audiofeels.data.playback

import com.trm.audiofeels.data.database.dao.PlaylistDao
import com.trm.audiofeels.data.database.model.PlaylistEntity
import com.trm.audiofeels.data.playback.mapper.toCarryOn
import com.trm.audiofeels.data.playback.mapper.toEntity
import com.trm.audiofeels.data.playback.mapper.toPlaylist
import com.trm.audiofeels.data.playback.mapper.toPlaylistPlayback
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback
import com.trm.audiofeels.domain.repository.PlaybackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

@Inject
class PlaybackLocalRepository(private val playlistDao: PlaylistDao) : PlaybackRepository {
  override suspend fun setNewCurrentPlaylist(playlist: Playlist, carryOn: Boolean) {
    playlistDao.setNewCurrentPlaylist(playlist.toEntity(), carryOn)
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

  override fun getCarryOnPlaylistsFlow(): Flow<List<CarryOnPlaylist>> =
    playlistDao.selectAllOrderByLastPlayed().map { it.map(PlaylistEntity::toCarryOn) }

  override fun getFavouritePlaylistsFlow(): Flow<List<Playlist>> =
    playlistDao.selectFavouritePlaylists().map { it.map(PlaylistEntity::toPlaylist) }
}
