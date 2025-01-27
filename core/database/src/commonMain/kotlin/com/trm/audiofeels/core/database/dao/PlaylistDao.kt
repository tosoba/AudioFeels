package com.trm.audiofeels.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trm.audiofeels.core.database.model.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(playlist: PlaylistEntity)

  @Query("SELECT * FROM playlist ORDER BY lastPlayed DESC")
  fun selectAllOrderByLastPlayed(): Flow<List<PlaylistEntity>>
}
