package com.trm.audiofeels.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.trm.audiofeels.core.database.model.PlaylistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Dao
interface PlaylistDao {
  @Upsert suspend fun upsert(playlist: PlaylistEntity)

  @Query("SELECT * FROM playlist WHERE id = :id") fun selectPlaylistById(id: String): PlaylistEntity?

  @Query("SELECT * FROM playlist WHERE lastPlayed IS NOT NULL ORDER BY lastPlayed DESC")
  fun selectAllOrderByLastPlayed(): Flow<List<PlaylistEntity>>

  @Query("SELECT * FROM playlist WHERE lastPlayed IS NULL LIMIT 1")
  fun selectCurrentPlaylist(): Flow<PlaylistEntity?>

  @Query("UPDATE playlist SET lastPlayed = :lastPlayed, autoPlay = FALSE WHERE lastPlayed IS NULL")
  suspend fun clearCurrentPlaylist(lastPlayed: Instant)

  @Query("UPDATE playlist SET lastPlayed = NULL, autoPlay = TRUE WHERE id = :id")
  suspend fun setNewCurrentPlaylist(id: String)

  @Transaction
  suspend fun setNewCurrentPlaylist(playlist: PlaylistEntity) {
    clearCurrentPlaylist(Clock.System.now())
    selectPlaylistById(playlist.id)?.let { setNewCurrentPlaylist(it.id) }
      ?: run { upsert(playlist) }
  }
}
