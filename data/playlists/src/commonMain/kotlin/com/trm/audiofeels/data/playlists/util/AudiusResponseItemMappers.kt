package com.trm.audiofeels.data.playlists.util

import com.trm.audiofeels.api.audius.model.Artwork
import com.trm.audiofeels.api.audius.model.PlaylistsResponseItem
import com.trm.audiofeels.api.audius.model.TrackResponseItem
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

fun PlaylistsResponseItem.toPlaylist() =
  Playlist(
    id = requireNotNull(id),
    name = requireNotNull(playlistName),
    description = description,
    artworkUrl = artwork?.url,
    score = score ?: 0.0,
    trackCount = requireNotNull(trackCount),
  )

fun TrackResponseItem.toTrack() =
  Track(
    artworkUrl = artwork?.url,
    description = description,
    duration = requireNotNull(duration),
    genre = genre,
    id = requireNotNull(id),
    mood = mood,
    playCount = playCount,
    tags = tags,
    title = requireNotNull(title),
  )

private val Artwork.url: String?
  get() = x1000 ?: x480 ?: x150
