package com.trm.audiofeels.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.trm.audiofeels.data.database.model.PlaylistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Dao
interface PlaylistDao {
  @Upsert suspend fun upsert(playlist: PlaylistEntity)

  @Query(
    "UPDATE playlist SET currentTrackIndex = :currentTrackIndex, currentTrackPositionMs = :currentTrackPositionMs, autoPlay = FALSE WHERE id = :id"
  )
  suspend fun updateCurrentPlaylist(
    id: String,
    currentTrackIndex: Int,
    currentTrackPositionMs: Long,
  )

  @Query("SELECT * FROM playlist WHERE id = :id")
  suspend fun selectPlaylistById(id: String): PlaylistEntity?

  @Query("SELECT * FROM playlist WHERE lastPlayed IS NOT NULL ORDER BY lastPlayed DESC")
  fun selectAllOrderByLastPlayed(): Flow<List<PlaylistEntity>>

  @Query("SELECT * FROM playlist WHERE lastPlayed IS NULL LIMIT 1")
  fun selectCurrentPlaylist(): Flow<PlaylistEntity?>

  @Query(
    "UPDATE playlist SET favourite = CASE WHEN favourite = TRUE THEN FALSE ELSE TRUE END WHERE lastPlayed IS NULL"
  )
  suspend fun toggleCurrentPlaylistFavourite()

  @Query("UPDATE playlist SET lastPlayed = :lastPlayed, autoPlay = FALSE WHERE lastPlayed IS NULL")
  suspend fun clearCurrentPlaylist(lastPlayed: Instant)

  @Query("UPDATE playlist SET lastPlayed = NULL, autoPlay = TRUE WHERE id = :id")
  suspend fun setNewCurrentPlaylistCarryOn(id: String)

  @Query(
    "UPDATE playlist SET lastPlayed = NULL, autoPlay = TRUE, currentTrackIndex = 0, currentTrackPositionMs = 0 WHERE id = :id"
  )
  suspend fun setNewCurrentPlaylist(id: String)

  @Transaction
  suspend fun setNewCurrentPlaylist(playlist: PlaylistEntity, carryOn: Boolean) {
    clearCurrentPlaylist(Clock.System.now())
    selectPlaylistById(playlist.id)?.let {
      if (carryOn) {
        setNewCurrentPlaylistCarryOn(it.id)
      } else {
        setNewCurrentPlaylist(it.id)
      }
    } ?: run { upsert(playlist) }
  }

  @Query("SELECT * FROM playlist WHERE favourite = TRUE")
  fun selectFavouritePlaylists(): Flow<List<PlaylistEntity>>
}
