package com.trm.audiofeels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.trm.audiofeels.AppRoute.DiscoverGraph
import com.trm.audiofeels.AppRoute.SearchPage
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.discover
import com.trm.audiofeels.core.ui.resources.search
import com.trm.audiofeels.domain.model.Mood
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

val APP_ROUTES = listOf(DiscoverGraph, SearchPage)

sealed interface AppRoute {
  val icon: ImageVector
  val labelResource: StringResource

  fun isSelected(currentDestination: NavDestination?): Boolean

  @Serializable
  data object DiscoverGraph : AppRoute {
    override val icon: ImageVector
      get() = Icons.Outlined.Explore

    override val labelResource: StringResource
      get() = Res.string.discover

    override fun isSelected(currentDestination: NavDestination?): Boolean =
      currentDestination?.hasRoute<SearchPage>() == false
  }

  @Serializable
  data object SearchPage : AppRoute {
    override val icon: ImageVector
      get() = Icons.Outlined.Search

    override val labelResource: StringResource
      get() = Res.string.search

    override fun isSelected(currentDestination: NavDestination?): Boolean =
      currentDestination?.hasRoute<SearchPage>() == true
  }
}

sealed interface DiscoverGraphRoute {
  @Serializable data object DiscoverPage : DiscoverGraphRoute

  @Serializable data object CarryOnPlaylistsPage : DiscoverGraphRoute

  @Serializable data class MoodPage(val mood: Mood) : DiscoverGraphRoute

  @Serializable data object MoodsPage : DiscoverGraphRoute

  @Serializable data object FavouritePlaylistsPage : DiscoverGraphRoute

  @Serializable data object TrendingPlaylistsPage : DiscoverGraphRoute
}
