package com.trm.audiofeels.domain.model

data class PlayerInput(val tracks: List<Track>, val host: String, val start: PlaybackStart) {
  val artworkUrl: String?
    get() = tracks.getOrNull(start.trackIndex)?.artworkUrl
}
