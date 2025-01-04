package com.trm.audiofeels.ui.player

import androidx.lifecycle.SavedStateHandle
import com.trm.audiofeels.domain.model.Playlist

private const val PLAYLIST_ID = "playlist_id"
private const val PLAYLIST_NAME = "playlist_name"
private const val PLAYLIST_DESCRIPTION = "playlist_description"
private const val PLAYLIST_ARTWORK_URL = "playlist_artwork_url"
private const val PLAYLIST_SCORE = "playlist_score"
private const val PLAYLIST_TRACK_COUNT = "playlist_track_count"

fun SavedStateHandle.setPlaylist(playlist: Playlist) {
  this[PLAYLIST_ID] = playlist.id
  this[PLAYLIST_NAME] = playlist.name
  this[PLAYLIST_DESCRIPTION] = playlist.description
  this[PLAYLIST_ARTWORK_URL] = playlist.artworkUrl
  this[PLAYLIST_SCORE] = playlist.score
  this[PLAYLIST_TRACK_COUNT] = playlist.trackCount
}

fun SavedStateHandle.getPlaylist(): Playlist? =
  get<String>(PLAYLIST_ID)?.let {
    Playlist(
      id = it,
      name = requireNotNull(get(PLAYLIST_NAME)),
      description = get(PLAYLIST_DESCRIPTION),
      artworkUrl = get(PLAYLIST_ARTWORK_URL),
      score = requireNotNull(get(PLAYLIST_SCORE)),
      trackCount = requireNotNull(get(PLAYLIST_TRACK_COUNT)),
    )
  }
