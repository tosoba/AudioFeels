package com.trm.audiofeels.core.network

fun buildTrackStreamUrl(id: String, host: String): String = "$host/v1/tracks/${id}/stream"
