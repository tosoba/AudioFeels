package com.trm.audiofeels.api.audius.model

import kotlinx.serialization.SerialName

data class PlaylistResponseItem(
  @SerialName("artwork") val artwork: Artwork?,
  @SerialName("description") val description: String?,
  @SerialName("favorite_count") val favoriteCount: Int?,
  @SerialName("id") val id: String?,
  @SerialName("playlist_name") val playlistName: String?,
  @SerialName("total_play_count") val totalPlayCount: Int?,
  @SerialName("track_count") val trackCount: Int?,
  @SerialName("tracks") val tracks: List<TrackResponseItem>?,
)
