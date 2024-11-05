package com.trm.audiofeels.api.audius.model

import kotlinx.serialization.SerialName

data class PlaylistResponse(@SerialName("data") val items: List<PlaylistResponseItem>?)
