package com.trm.audiofeels.data.test

import com.trm.audiofeels.core.test.platformTestContext
import com.trm.audiofeels.data.database.createApplicationInMemoryDatabase
import com.trm.audiofeels.data.playback.PlaybackLocalRepository
import com.trm.audiofeels.domain.repository.PlaybackRepository

fun playbackInMemoryRepository(): PlaybackRepository =
  PlaybackLocalRepository(
    playlistDao = createApplicationInMemoryDatabase(platformTestContext()).playlistDao()
  )
