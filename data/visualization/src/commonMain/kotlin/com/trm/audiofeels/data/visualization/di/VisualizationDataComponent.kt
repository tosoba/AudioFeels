package com.trm.audiofeels.data.visualization.di

import com.trm.audiofeels.data.visualization.VisualizationLocalRepository
import com.trm.audiofeels.domain.repository.VisualizationRepository
import me.tatarka.inject.annotations.Provides

interface VisualizationDataComponent {
  @Provides
  fun bindVisualizationRepository(
    repository: VisualizationLocalRepository
  ): VisualizationRepository = repository
}
