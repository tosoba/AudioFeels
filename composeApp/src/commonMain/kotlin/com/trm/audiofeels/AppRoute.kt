package com.trm.audiofeels

import com.trm.audiofeels.domain.model.Mood
import kotlinx.serialization.Serializable

sealed interface AppRoute {
  @Serializable data object DiscoverPage : AppRoute

  @Serializable data object SearchPage : AppRoute

  @Serializable data class MoodPage(val mood: Mood) : AppRoute
}
