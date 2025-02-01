package com.trm.audiofeels.domain.model

data class PlayerInput(val tracks: List<Track>, val host: String, val start: PlaybackStart) {
  val track: Track?
    get() = tracks.getOrNull(start.trackIndex)

  val artworkUrl: String?
    get() = track?.artworkUrl
}
