package com.trm.audiofeels.api.audius.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artwork(
  @SerialName("1000x1000") val x1000: String?,
  @SerialName("150x150") val x150: String?,
  @SerialName("480x480") val x480: String?,
)
