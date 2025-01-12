package com.trm.audiofeels.domain.repository

import com.trm.audiofeels.domain.model.PlaybackStart
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
  suspend fun updatePlaybackPlaylist(playlist: Playlist)

  fun getPlaybackPlaylistFlow(): Flow<Playlist?>

  suspend fun getPlaybackStart(): PlaybackStart

  suspend fun updatePlaybackTrack(track: Track, trackIndex: Int)

  fun getPlaybackTrackFlow(): Flow<Track?>

  suspend fun clear()
}
