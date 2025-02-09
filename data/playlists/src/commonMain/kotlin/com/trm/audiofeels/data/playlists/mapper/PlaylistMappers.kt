package com.trm.audiofeels.data.playlists.mapper

import com.trm.audiofeels.core.database.model.PlaylistEntity
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback

internal fun Playlist.toCurrentPlaylistEntity(): PlaylistEntity =
  PlaylistEntity(
    id = id,
    name = name,
    description = description,
    artworkUrl = artworkUrl,
    score = score,
    trackCount = trackCount,
  )

internal fun PlaylistPlayback.toCurrentPlaylistEntity(): PlaylistEntity =
  PlaylistEntity(
    id = playlist.id,
    name = playlist.name,
    description = playlist.description,
    artworkUrl = playlist.artworkUrl,
    score = playlist.score,
    trackCount = playlist.trackCount,
    currentTrackIndex = currentTrackIndex,
    currentTrackPositionMs = currentTrackPositionMs,
    autoPlay = autoPlay,
  )

internal fun PlaylistEntity.toCarryOn(): CarryOnPlaylist =
  CarryOnPlaylist(playlist = toPlaylist(), lastPlayed = requireNotNull(lastPlayed))

internal fun PlaylistEntity.toPlaylist(): Playlist =
  Playlist(
    id = id,
    name = name,
    description = description,
    artworkUrl = artworkUrl,
    score = score,
    trackCount = trackCount,
  )

internal fun PlaylistEntity.toPlaylistPlayback(): PlaylistPlayback =
  PlaylistPlayback(
    playlist =
      Playlist(
        id = id,
        name = name,
        description = description,
        artworkUrl = artworkUrl,
        score = score,
        trackCount = trackCount,
      ),
    currentTrackIndex = currentTrackIndex,
    currentTrackPositionMs = currentTrackPositionMs,
    autoPlay = autoPlay,
  )
