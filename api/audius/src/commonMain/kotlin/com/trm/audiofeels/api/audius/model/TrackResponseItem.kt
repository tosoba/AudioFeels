package com.trm.audiofeels.api.audius.model

import kotlinx.serialization.SerialName

data class TrackResponseItem(
  @SerialName("artwork") val artwork: Artwork?,
  @SerialName("description") val description: String?,
  @SerialName("duration") val duration: Int?,
  @SerialName("genre") val genre: String?,
  @SerialName("id") val id: String?,
  @SerialName("is_available") val isAvailable: Boolean?,
  @SerialName("is_delete") val isDelete: Boolean?,
  @SerialName("is_premium") val isPremium: Boolean?,
  @SerialName("is_streamable") val isStreamable: Boolean?,
  @SerialName("mood") val mood: String?,
  @SerialName("play_count") val playCount: Int?,
  @SerialName("tags") val tags: String?,
  @SerialName("title") val title: String?,
)
