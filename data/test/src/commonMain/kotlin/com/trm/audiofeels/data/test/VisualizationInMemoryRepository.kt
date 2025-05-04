package com.trm.audiofeels.data.test

import com.trm.audiofeels.core.test.InMemoryDataStorePreferences
import com.trm.audiofeels.data.visualization.VisualizationLocalRepository
import com.trm.audiofeels.domain.repository.VisualizationRepository

fun visualizationInMemoryRepository(): VisualizationRepository =
    VisualizationLocalRepository(InMemoryDataStorePreferences())
