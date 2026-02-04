package com.trm.audiofeels.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable data class CarryOnPlaylist(val playlist: Playlist, val lastPlayed: Instant)
