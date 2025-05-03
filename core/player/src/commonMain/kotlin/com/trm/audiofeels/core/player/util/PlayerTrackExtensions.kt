package com.trm.audiofeels.core.player.util

import com.trm.audiofeels.domain.model.Track

fun Track.buildStreamUrl(host: String): String = "$host/v1/tracks/${id}/stream"
