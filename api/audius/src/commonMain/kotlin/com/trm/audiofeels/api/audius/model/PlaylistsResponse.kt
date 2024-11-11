package com.trm.audiofeels.api.audius.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistsResponse(@SerialName("data") val items: List<PlaylistsResponseItem>?)
