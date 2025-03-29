package com.trm.audiofeels.data.suggestions.di

import com.trm.audiofeels.data.suggestions.SuggestionsLocalRepository
import com.trm.audiofeels.domain.repository.SuggestionsRepository
import me.tatarka.inject.annotations.Provides

interface SuggestionsDataComponent {
  @Provides
  fun bindSuggestionsRepository(repository: SuggestionsLocalRepository): SuggestionsRepository =
    repository
}
