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
    favourite = favourite,
  )

internal fun PlaylistEntity.toCarryOn(): CarryOnPlaylist =
  CarryOnPlaylist(playlist = toPlaylist(), lastPlayed = requireNotNull(lastPlayed))

internal fun PlaylistEntity.toPlaylistPlayback(): PlaylistPlayback =
  PlaylistPlayback(
    playlist = toPlaylist(),
    currentTrackIndex = currentTrackIndex,
    currentTrackPositionMs = currentTrackPositionMs,
    autoPlay = autoPlay,
  )

internal fun PlaylistEntity.toPlaylist(): Playlist =
  Playlist(
    id = id,
    name = name,
    description = description,
    artworkUrl = artworkUrl,
    score = score,
    trackCount = trackCount,
    favourite = favourite,
  )
