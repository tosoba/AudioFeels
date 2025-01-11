package com.trm.audiofeels.data.playback

import com.trm.audiofeels.domain.model.Playback
import com.trm.audiofeels.domain.repository.PlaybackRepository

class AudiusPlaybackRepository : PlaybackRepository {
  override suspend fun updatePlayback(playback: Playback) {}
}
