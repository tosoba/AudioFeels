package com.trm.audiofeels.data.playback

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.preferences.getFlow
import com.trm.audiofeels.core.preferences.playbackAutoPlayPreferenceKey
import com.trm.audiofeels.core.preferences.playbackPlaylistPreferenceKey
import com.trm.audiofeels.core.preferences.playbackTrackIndexPreferenceKey
import com.trm.audiofeels.core.preferences.playbackTrackPreferenceKey
import com.trm.audiofeels.domain.model.PlaybackStart
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
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
          autoPlay = preferences[playbackAutoPlayPreferenceKey] ?: false,
        )
      }
      .firstOrNull() ?: PlaybackStart()

  override suspend fun updatePlaybackTrack(track: Track, trackIndex: Int) {
    dataStore.edit { preferences ->
      preferences[playbackTrackPreferenceKey] = Json.encodeToString(Track.serializer(), track)
      preferences[playbackTrackIndexPreferenceKey] = trackIndex
      preferences[playbackAutoPlayPreferenceKey] = false
    }
  }

  override fun getPlaybackTrackFlow(): Flow<Track?> =
    dataStore.getFlow(playbackTrackPreferenceKey).map { trackJson ->
      trackJson?.let { Json.decodeFromString(Track.serializer(), it) }
    }

  override suspend fun clear() {
    dataStore.edit { preferences ->
      preferences -= playbackPlaylistPreferenceKey
      preferences -= playbackTrackPreferenceKey
      preferences[playbackTrackIndexPreferenceKey] = 0
      preferences[playbackAutoPlayPreferenceKey] = false
    }
  }
}
