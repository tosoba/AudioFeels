package com.trm.audiofeels.domain.repository

import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
  suspend fun updatePlaybackPlaylist(playlist: Playlist)

  fun getPlaybackPlaylistFlow(): Flow<Playlist?>

  suspend fun getPlaybackTrackIndex(): Int

  suspend fun updatePlaybackTrack(track: Track, trackIndex: Int)

  suspend fun clear()
}
