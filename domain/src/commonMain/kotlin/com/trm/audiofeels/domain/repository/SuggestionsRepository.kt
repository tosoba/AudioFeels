package com.trm.audiofeels.domain.repository

import kotlinx.coroutines.flow.Flow

interface SuggestionsRepository {
  fun getSuggestionsFlow(limit: Int): Flow<List<String>>

  suspend fun saveSuggestion(suggestion: String)
}
