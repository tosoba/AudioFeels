package com.trm.audiofeels.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.trm.audiofeels.data.database.model.SuggestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SuggestionDao {
  @Upsert suspend fun upsert(suggestion: SuggestionEntity)

  @Query("SELECT * FROM suggestion ORDER BY lastSearched DESC LIMIT :limit")
  fun selectMostRecentSuggestions(limit: Int): Flow<List<SuggestionEntity>>
}
