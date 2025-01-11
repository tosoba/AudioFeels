package com.trm.audiofeels.core.preferences

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val hostPreferenceKey = stringPreferencesKey("host")

val playbackPlaylistPreferenceKey = stringPreferencesKey("playbackPlaylist")
val playbackTrackPreferenceKey = stringPreferencesKey("playbackTrack")
val playbackTrackIndexPreferenceKey = intPreferencesKey("playbackTrackIndex")
