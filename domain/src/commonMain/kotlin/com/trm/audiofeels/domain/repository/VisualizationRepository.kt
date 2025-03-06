package com.trm.audiofeels.domain.repository

interface VisualizationRepository {
  suspend fun savePermissionPermanentlyDenied(denied: Boolean)

  suspend fun isPermissionPermanentlyDenied(): Boolean
}
