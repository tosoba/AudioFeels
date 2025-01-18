package com.trm.audiofeels.data.playback

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.preferences.getFlow
import com.trm.audiofeels.core.preferences.playbackAutoPlayPreferenceKey
import com.trm.audiofeels.core.preferences.playbackPlaylistPreferenceKey
import com.trm.audiofeels.core.preferences.playbackTrackIndexPreferenceKey
import com.trm.audiofeels.core.preferences.playbackTrackPositionMsKey
import com.trm.audiofeels.domain.model.PlaybackStart
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.repository.PlaybackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class AudiusPlaybackRepository(private val dataStore: DataStore<Preferences>) : PlaybackRepository {
  override suspend fun updatePlaybackPlaylist(playlist: Playlist) {
    dataStore.edit { preferences ->
      preferences[playbackPlaylistPreferenceKey] =
        Json.encodeToString(Playlist.serializer(), playlist)
      preferences[playbackTrackIndexPreferenceKey] = 0
      preferences[playbackAutoPlayPreferenceKey] = true
    }
  }

  override fun getPlaybackPlaylistFlow(): Flow<Playlist?> =
    dataStore.getFlow(playbackPlaylistPreferenceKey).map { playlistJson ->
      playlistJson?.let { Json.decodeFromString(Playlist.serializer(), it) }
    }

  override suspend fun getPlaybackStart(): PlaybackStart =
    dataStore.data
      .map { preferences ->
        PlaybackStart(
          trackIndex = preferences[playbackTrackIndexPreferenceKey] ?: 0,
          trackPositionMs = preferences[playbackTrackPositionMsKey] ?: 0L,
          autoPlay = preferences[playbackAutoPlayPreferenceKey] ?: false,
        )
      }
      .firstOrNull() ?: PlaybackStart()

  override suspend fun updatePlaybackTrack(trackIndex: Int, trackPositionMs: Long) {
    dataStore.edit { preferences ->
      preferences[playbackTrackIndexPreferenceKey] = trackIndex
      preferences[playbackTrackPositionMsKey] = trackPositionMs
      preferences[playbackAutoPlayPreferenceKey] = false
    }
  }

  override suspend fun clear() {
    dataStore.edit { preferences ->
      preferences -= playbackPlaylistPreferenceKey
      preferences[playbackTrackIndexPreferenceKey] = 0
      preferences[playbackAutoPlayPreferenceKey] = false
    }
  }
}
