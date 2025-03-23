package com.trm.audiofeels.data.playlists.util

import com.trm.audiofeels.api.audius.model.Artwork
import com.trm.audiofeels.api.audius.model.PlaylistResponse
import com.trm.audiofeels.api.audius.model.PlaylistsResponse
import com.trm.audiofeels.api.audius.model.PlaylistsResponseItem
import com.trm.audiofeels.api.audius.model.TrackResponseItem
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

internal fun PlaylistsResponse.toPlaylists(): List<Playlist> =
  items?.filter(PlaylistsResponseItem::isValid)?.map(PlaylistsResponseItem::toPlaylist).orEmpty()

internal fun PlaylistsResponseItem.toPlaylist() =
  Playlist(
    id = requireNotNull(id),
    name = requireNotNull(playlistName),
    description = description,
    artworkUrl = artwork?.url,
    score = score ?: 0.0,
    trackCount = requireNotNull(trackCount),
    favourite = false,
  )

internal fun PlaylistResponse.toTracks(): List<Track> =
  items
    ?.firstOrNull()
    ?.tracks
    ?.filter(TrackResponseItem::isValid)
    ?.map(TrackResponseItem::toTrack)
    .orEmpty()

internal fun TrackResponseItem.toTrack() =
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
