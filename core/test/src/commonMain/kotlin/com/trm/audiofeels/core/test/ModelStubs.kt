package com.trm.audiofeels.core.test

import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

fun stubPlaylist(
  id: String = "",
  name: String = "",
  description: String? = null,
  artworkUrl: String? = null,
  score: Double = 0.0,
  trackCount: Int = 0,
  favourite: Boolean = false,
): Playlist =
  Playlist(
    id = id,
    name = name,
    description = description,
    artworkUrl = artworkUrl,
    score = score,
    trackCount = trackCount,
    favourite = favourite,
  )

fun stubTrack(
  artworkUrl: String? = null,
  description: String? = null,
  duration: Int = 60,
  genre: String? = null,
  id: String = "",
  mood: String? = null,
  playCount: Int? = null,
  tags: String? = null,
  title: String = "",
): Track =
  Track(
    artworkUrl = artworkUrl,
    description = description,
    duration = duration,
    genre = genre,
    id = id,
    mood = mood,
    playCount = playCount,
    tags = tags,
    title = title,
  )
