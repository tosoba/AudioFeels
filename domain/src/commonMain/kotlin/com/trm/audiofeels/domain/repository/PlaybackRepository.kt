package com.trm.audiofeels.domain.repository

import com.trm.audiofeels.domain.model.Playback

interface PlaybackRepository {
  suspend fun updatePlayback(playback: Playback)
}
