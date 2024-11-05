package com.trm.audiofeels.api.audius.model

import kotlinx.serialization.SerialName

data class PlaylistsResponse(@SerialName("data") val items: List<PlaylistsResponseItem>?)
