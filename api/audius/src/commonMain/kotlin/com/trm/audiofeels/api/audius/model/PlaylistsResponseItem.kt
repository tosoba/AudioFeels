package com.trm.audiofeels.api.audius.model

import kotlinx.serialization.SerialName

data class PlaylistsResponseItem(
  @SerialName("artwork") val artwork: Artwork?,
  @SerialName("description") val description: String?,
  @SerialName("id") val id: String?,
  @SerialName("playlist_name") val playlistName: String?,
  @SerialName("score") val score: Double?,
  @SerialName("track_count") val trackCount: Int?,
)
