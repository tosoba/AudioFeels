package com.trm.audiofeels.api.audius.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistResponse(@SerialName("data") val items: List<PlaylistResponseItem>?)
