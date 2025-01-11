package com.trm.audiofeels.data.playback.di

import com.trm.audiofeels.data.playback.AudiusPlaybackRepository
import com.trm.audiofeels.domain.repository.PlaybackRepository
import me.tatarka.inject.annotations.Provides

interface PlaybackDataComponent {
  @Provides
  fun bindPlaybackRepository(repository: AudiusPlaybackRepository): PlaybackRepository = repository
}
