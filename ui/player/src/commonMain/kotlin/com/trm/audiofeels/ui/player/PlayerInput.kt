package com.trm.audiofeels.ui.player

import com.trm.audiofeels.domain.model.PlaybackStart
import com.trm.audiofeels.domain.model.Track

data class PlayerInput(val tracks: List<Track>, val host: String, val start: PlaybackStart)