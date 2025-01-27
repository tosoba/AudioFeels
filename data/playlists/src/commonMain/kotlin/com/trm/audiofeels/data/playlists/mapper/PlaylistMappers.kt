package com.trm.audiofeels.data.playlists.mapper

import com.trm.audiofeels.core.database.model.PlaylistEntity
import com.trm.audiofeels.domain.model.Playlist
import kotlinx.datetime.Clock

fun Playlist.toEntity(): PlaylistEntity =
  PlaylistEntity(
    id = id,
    name = name,
    description = description,
    artworkUrl = artworkUrl,
    score = score,
    trackCount = trackCount,
    lastPlayed = Clock.System.now(),
  )
