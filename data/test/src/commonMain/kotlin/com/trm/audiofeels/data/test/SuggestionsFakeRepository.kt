package com.trm.audiofeels.data.test

import com.trm.audiofeels.domain.repository.SuggestionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SuggestionsFakeRepository(initialSuggestions: List<String> = emptyList()) :
  SuggestionsRepository {
  private val flow = MutableStateFlow(initialSuggestions)

  override fun getSuggestionsFlow(limit: Int): Flow<List<String>> = flow.asStateFlow()

  override suspend fun saveSuggestion(suggestion: String) {
    flow.update { it + suggestion }
  }
}
