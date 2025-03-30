package com.trm.audiofeels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.discover
import com.trm.audiofeels.core.ui.resources.search
import org.jetbrains.compose.resources.StringResource

data class AppPageNavigationDestination(
  val route: AppGraphRoute,
  val icon: ImageVector,
  val labelResource: StringResource,
)

val PAGE_NAVIGATION_DESTINATIONS =
  listOf(
    AppPageNavigationDestination(
      route = AppGraphRoute.DiscoverGraph,
      icon = Icons.Outlined.Explore,
      labelResource = Res.string.discover,
    ),
    AppPageNavigationDestination(
      route = AppGraphRoute.SearchPage,
      icon = Icons.Outlined.Search,
      labelResource = Res.string.search,
    ),
  )
