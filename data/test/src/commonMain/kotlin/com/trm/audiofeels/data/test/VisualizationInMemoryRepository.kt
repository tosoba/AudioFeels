package com.trm.audiofeels.data.test

import com.trm.audiofeels.core.test.InMemoryDataStorePreferences
import com.trm.audiofeels.data.visualization.VisualizationLocalRepository
import com.trm.audiofeels.domain.repository.VisualizationRepository

fun visualizationInMemoryRepository(
  permissionPermanentlyDenied: Boolean? = null
): VisualizationRepository =
  VisualizationLocalRepository(
    permissionPermanentlyDenied?.let {
      InMemoryDataStorePreferences(
        VisualizationLocalRepository.permissionPermanentlyDeniedPreferenceKey to it
      )
    } ?: InMemoryDataStorePreferences()
  )
