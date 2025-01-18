package com.trm.audiofeels.domain.repository

import com.trm.audiofeels.domain.model.PlaybackStart
import com.trm.audiofeels.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
  suspend fun updatePlaybackPlaylist(playlist: Playlist)

  fun getPlaybackPlaylistFlow(): Flow<Playlist?>

  suspend fun getPlaybackStart(): PlaybackStart

  suspend fun updatePlaybackTrack(trackIndex: Int, trackPositionMs: Long)

  suspend fun clear()
}
