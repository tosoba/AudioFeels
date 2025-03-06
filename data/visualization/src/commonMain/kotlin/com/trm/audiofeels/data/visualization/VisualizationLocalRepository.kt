package com.trm.audiofeels.data.visualization

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.trm.audiofeels.core.preferences.get
import com.trm.audiofeels.core.preferences.set
import com.trm.audiofeels.domain.repository.VisualizationRepository
import me.tatarka.inject.annotations.Inject

@Inject
class VisualizationLocalRepository(private val dataStore: DataStore<Preferences>) :
  VisualizationRepository {
  override suspend fun savePermissionPermanentlyDenied(denied: Boolean) {
    dataStore.set(permissionPermanentlyDeniedPreferenceKey, denied)
  }

  override suspend fun isPermissionPermanentlyDenied(): Boolean =
    dataStore.get(permissionPermanentlyDeniedPreferenceKey) ?: false

  companion object {
    val permissionPermanentlyDeniedPreferenceKey =
      booleanPreferencesKey("permissionPermanentlyDenied")
  }
}
