package com.trm.audiofeels.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable data class CarryOnPlaylist(val playlist: Playlist, val lastPlayed: Instant)
