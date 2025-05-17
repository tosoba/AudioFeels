package com.trm.audiofeels.data.test

import com.trm.audiofeels.core.test.platformTestContext
import com.trm.audiofeels.data.database.ApplicationDatabase
import com.trm.audiofeels.data.database.createApplicationInMemoryDatabase
import com.trm.audiofeels.data.playback.PlaybackLocalRepository
import com.trm.audiofeels.domain.repository.PlaybackRepository

class PlaybackInMemoryRepository
private constructor(
  private val database: ApplicationDatabase,
  private val repository: PlaybackRepository,
) : PlaybackRepository by repository, AutoCloseable {
  override fun close() {
    database.close()
  }

  companion object {
    operator fun invoke(): PlaybackInMemoryRepository {
      val database = createApplicationInMemoryDatabase(platformTestContext())
      return PlaybackInMemoryRepository(
        database = database,
        repository = PlaybackLocalRepository(playlistDao = database.playlistDao()),
      )
    }
  }
}
