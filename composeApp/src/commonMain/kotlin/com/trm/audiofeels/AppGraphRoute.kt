package com.trm.audiofeels

import com.trm.audiofeels.domain.model.Mood
import kotlinx.serialization.Serializable

sealed interface AppGraphRoute {
  @Serializable data object DiscoverGraph : AppGraphRoute

  @Serializable data object SearchPage : AppGraphRoute
}

sealed interface DiscoverGraphRoute {
  @Serializable data object DiscoverPage : DiscoverGraphRoute

  @Serializable data object CarryOnPlaylistsPage : DiscoverGraphRoute

  @Serializable data class MoodPage(val mood: Mood) : DiscoverGraphRoute

  @Serializable data object MoodsPage : DiscoverGraphRoute

  @Serializable data object FavouritePlaylistsPage : DiscoverGraphRoute

  @Serializable data object TrendingPlaylistsPage : DiscoverGraphRoute
}
