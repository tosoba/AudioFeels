package com.trm.audiofeels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trm.audiofeels.core.ui.compose.util.NavigationContentPosition
import com.trm.audiofeels.core.ui.compose.util.NavigationType
import com.trm.audiofeels.core.ui.compose.util.calculateWindowSize
import com.trm.audiofeels.ui.discover.DiscoverPage
import com.trm.audiofeels.ui.favourites.FavouritesPage
import com.trm.audiofeels.ui.search.SearchPage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AppContent() {
  MaterialTheme {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val navigationType =
      NavigationType(
        adaptiveInfo = adaptiveInfo,
        windowSize = with(LocalDensity.current) { calculateWindowSize().toDpSize() },
      )
    val navigationContentPosition =
      NavigationContentPosition(adaptiveInfo.windowSizeClass.windowHeightSizeClass)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
      NavigationSuiteScaffoldLayout(
        layoutType = navigationType.suiteType,
        navigationSuite = {
          when (navigationType) {
            NavigationType.NAVIGATION_BAR ->
              AppBottomNavigationBar(
                currentDestination = currentDestination,
                navigatePageDestination = navController::navigateToPageDestination,
              )

            NavigationType.NAVIGATION_RAIL ->
              AppNavigationRail(
                currentDestination = currentDestination,
                navigationContentPosition = navigationContentPosition,
                navigatePageDestination = navController::navigateToPageDestination,
              )

            NavigationType.PERMANENT_NAVIGATION_DRAWER ->
              AppPermanentNavigationDrawer(
                currentDestination = currentDestination,
                navigationContentPosition = navigationContentPosition,
                navigatePageDestination = navController::navigateToPageDestination,
              )
          }
        },
      ) {
        val scope = rememberCoroutineScope()
        val scaffoldState =
          rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
          )

        BottomSheetScaffold(
          sheetContent = {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.fillMaxWidth().height(100.dp),
            ) {
              Text("TEST")
            }
          },
          scaffoldState = scaffoldState,
        ) {
          AppNavHost(navController = navController, modifier = Modifier.fillMaxSize().padding(it))
        }
      }
    }
  }
}

@Composable
private fun AppBottomNavigationBar(
  currentDestination: NavDestination?,
  navigatePageDestination: (AppPageNavigationDestination) -> Unit,
) {
  NavigationBar(modifier = Modifier.fillMaxWidth()) {
    PAGE_NAVIGATION_DESTINATIONS.forEach { destination ->
      NavigationBarItem(
        selected = currentDestination?.hasRoute(destination.route::class) == true,
        onClick = { navigatePageDestination(destination) },
        icon = {
          Icon(
            imageVector = destination.icon,
            contentDescription = stringResource(destination.labelResource),
          )
        },
      )
    }
  }
}

@Composable
private fun AppNavigationRail(
  currentDestination: NavDestination?,
  navigationContentPosition: NavigationContentPosition,
  navigatePageDestination: (AppPageNavigationDestination) -> Unit,
) {
  NavigationRail(
    modifier = Modifier.fillMaxHeight(),
    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.inverseOnSurface,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      PAGE_NAVIGATION_DESTINATIONS.forEach { destination ->
        NavigationRailItem(
          selected = currentDestination?.hasRoute(destination.route::class) == true,
          onClick = { navigatePageDestination(destination) },
          icon = {
            Icon(
              imageVector = destination.icon,
              contentDescription = stringResource(destination.labelResource),
            )
          },
        )
      }
    }
  }
}

@Composable
private fun AppPermanentNavigationDrawer(
  currentDestination: NavDestination?,
  navigationContentPosition: NavigationContentPosition,
  navigatePageDestination: (AppPageNavigationDestination) -> Unit,
) {
  PermanentDrawerSheet(
    modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp),
    drawerContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHigh,
  ) {
    Surface(modifier = Modifier.padding(16.dp)) {
      Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        PAGE_NAVIGATION_DESTINATIONS.forEach { destination ->
          NavigationDrawerItem(
            selected = currentDestination?.hasRoute(destination.route::class) == true,
            label = {
              Text(
                text = stringResource(destination.labelResource),
                modifier = Modifier.padding(horizontal = 16.dp),
              )
            },
            icon = {
              Icon(
                imageVector = destination.icon,
                contentDescription = stringResource(destination.labelResource),
              )
            },
            colors =
              NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { navigatePageDestination(destination) },
          )
        }
      }
    }
  }
}

@Composable
private fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
  NavHost(
    modifier = modifier,
    navController = navController,
    startDestination = AppRoute.Discover,
  ) {
    composable<AppRoute.Discover> { DiscoverPage(modifier = Modifier.fillMaxSize()) }
    composable<AppRoute.Favourites> { FavouritesPage(modifier = Modifier.fillMaxSize()) }
    composable<AppRoute.Search> { SearchPage(modifier = Modifier.fillMaxSize()) }
  }
}

private fun NavController.navigateToPageDestination(destination: AppPageNavigationDestination) {
  navigate(destination.route) {
    popUpTo(graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
  }
}
