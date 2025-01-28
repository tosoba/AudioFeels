package com.trm.audiofeels.domain.model

import kotlinx.datetime.Instant

data class CarryOnPlaylist(val playlist: Playlist, val lastPlayed: Instant)
