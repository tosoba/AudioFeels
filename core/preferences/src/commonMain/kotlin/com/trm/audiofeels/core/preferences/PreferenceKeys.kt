package com.trm.audiofeels.core.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val hostPreferenceKey = stringPreferencesKey("host")

val playbackPlaylistPreferenceKey = stringPreferencesKey("playbackPlaylist")
val playbackTrackIndexPreferenceKey = intPreferencesKey("playbackTrackIndex")
val playbackAutoPlayPreferenceKey = booleanPreferencesKey("playbackAutoPlay")
val playbackTrackPositionMsKey = longPreferencesKey("playbackTrackPositionMs")
