package com.trm.audiofeels.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.trm.audiofeels.core.database.model.PlaylistEntity

@Dao
interface PlaylistDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(playlist: PlaylistEntity)
}
