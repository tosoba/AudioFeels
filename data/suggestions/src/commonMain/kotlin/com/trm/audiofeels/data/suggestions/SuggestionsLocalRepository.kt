package com.trm.audiofeels.data.suggestions

import com.trm.audiofeels.core.database.dao.SuggestionDao
import com.trm.audiofeels.core.database.model.SuggestionEntity
import com.trm.audiofeels.domain.repository.SuggestionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class SuggestionsLocalRepository(private val dao: SuggestionDao) : SuggestionsRepository {
  override fun getSuggestionsFlow(limit: Int): Flow<List<String>> =
    dao.selectMostRecentSuggestions(limit).map { it.map(SuggestionEntity::query) }

  override suspend fun saveSuggestion(suggestion: String) {
    dao.upsert(SuggestionEntity(suggestion))
  }
}
