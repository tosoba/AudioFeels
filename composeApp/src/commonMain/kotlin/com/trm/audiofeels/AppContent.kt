package com.trm.audiofeels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import coil3.compose.setSingletonImageLoaderFactory
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.ktx.rememberThemeColor
import com.trm.audiofeels.core.ui.compose.theme.GRADIENT_BASE_ALPHA
import com.trm.audiofeels.core.ui.compose.theme.GRADIENT_MAX_ALPHA
import com.trm.audiofeels.core.ui.compose.theme.Spacing
import com.trm.audiofeels.core.ui.compose.theme.UpdateEdgeToEdge
import com.trm.audiofeels.core.ui.compose.util.NavigationContentPosition
import com.trm.audiofeels.core.ui.compose.util.NavigationType
import com.trm.audiofeels.core.ui.compose.util.currentWindowHeightClass
import com.trm.audiofeels.core.ui.compose.util.defaultHazeEffect
import com.trm.audiofeels.core.ui.compose.util.loadImageBitmapOrNull
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.favourite
import com.trm.audiofeels.core.ui.resources.trending
import com.trm.audiofeels.di.ApplicationComponent
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.ui.discover.DiscoverPage
import com.trm.audiofeels.ui.discover.DiscoverViewModel
import com.trm.audiofeels.ui.mood.MoodPage
import com.trm.audiofeels.ui.mood.MoodViewModel
import com.trm.audiofeels.ui.moods.MoodsPage
import com.trm.audiofeels.ui.player.PlayerViewModel
import com.trm.audiofeels.ui.player.PlayerViewState
import com.trm.audiofeels.ui.player.composable.PlayerAudioVisualization
import com.trm.audiofeels.ui.player.composable.PlayerCancelPlaybackButton
import com.trm.audiofeels.ui.player.composable.PlayerExpandedContent
import com.trm.audiofeels.ui.player.composable.PlayerRecordAudioPermissionObserver
import com.trm.audiofeels.ui.player.composable.PlayerRecordAudioPermissionRequest
import com.trm.audiofeels.ui.player.composable.PlayerSheetContent
import com.trm.audiofeels.ui.player.util.currentTrackArtworkUrl
import com.trm.audiofeels.ui.player.util.isPlaying
import com.trm.audiofeels.ui.player.util.playerVisible
import com.trm.audiofeels.ui.playlists.CarryOnPlaylistsPage
import com.trm.audiofeels.ui.playlists.PlaylistsPage
import com.trm.audiofeels.ui.search.SearchPage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.zwander.compose.rememberThemeInfo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppContent(applicationComponent: ApplicationComponent) {
  setSingletonImageLoaderFactory { applicationComponent.imageLoader }

  UpdateEdgeToEdge(isSystemInDarkTheme())

  val playerViewModel =
    viewModel<PlayerViewModel>(factory = applicationComponent.playerViewModelFactory)
  val playerViewState by playerViewModel.playerViewState.collectAsStateWithLifecycle()
  val currentPlaylist by playerViewModel.currentPlaylist.collectAsStateWithLifecycle()

  val requestRecordAudioPermission by
    playerViewModel.requestRecordAudioPermission.collectAsStateWithLifecycle()
  if (requestRecordAudioPermission) {
    PlayerRecordAudioPermissionRequest(
      onDeniedPermanently = playerViewModel::onRecordAudioPermissionDeniedPermanently
    )
  }
  PlayerRecordAudioPermissionObserver(
    onGranted = playerViewModel::onRecordAudioPermissionGranted,
    onDenied = playerViewModel::onRecordAudioPermissionDenied,
  )

  DynamicMaterialTheme(seedColor = rememberThemeSeedColor(playerViewState, applicationComponent)) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val navigationType =
      NavigationType(adaptiveInfo = adaptiveInfo, windowSize = currentWindowDpSize())
    val navigationContentPosition = NavigationContentPosition(currentWindowHeightClass())

    val scope = rememberCoroutineScope()
    val appLayoutState =
      rememberAppLayoutState(
        playerVisible = playerViewState.playerVisible,
        playerViewState =
          rememberAppPlayerLayoutState(
            scaffoldState =
              rememberBottomSheetScaffoldState(
                bottomSheetState =
                  rememberStandardBottomSheetState(
                    initialValue = SheetValue.Hidden,
                    skipHiddenState = false,
                  )
              )
          ),
      )

    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    fun navigateToAppGraphRoute(route: AppRoute) {
      scope.launch { appLayoutState.onNavigateToPageDestination() }
      navController.navigateToAppRoute(route)
    }

    NavigationSuiteScaffoldLayout(
      layoutType = navigationType.suiteType,
      navigationSuite = {
        when (navigationType) {
          NavigationType.NAVIGATION_BAR -> {
            AppBottomNavigationBar(
              currentDestination = currentDestination,
              onNavigateToRoute = ::navigateToAppGraphRoute,
            )
          }
          NavigationType.NAVIGATION_RAIL -> {
            AppNavigationRail(
              currentDestination = currentDestination,
              navigationContentPosition = navigationContentPosition,
              onNavigateToRoute = ::navigateToAppGraphRoute,
            )
          }
          NavigationType.PERMANENT_NAVIGATION_DRAWER -> {
            AppPermanentNavigationDrawer(
              currentDestination = currentDestination,
              navigationContentPosition = navigationContentPosition,
              onNavigateToRoute = ::navigateToAppGraphRoute,
            )
          }
        }
      },
    ) {
      AppBottomSheetScaffold(
        appLayoutState = appLayoutState,
        playerViewState = playerViewState,
        currentPlaylist = currentPlaylist,
        navController = navController,
        applicationComponent = applicationComponent,
      )
    }

    val audioData by playerViewModel.audioData.collectAsStateWithLifecycle()
    AnimatedVisibility(
      visible = audioData != null && playerViewState is PlayerViewState.Playback,
      enter = fadeIn(),
      exit = fadeOut(),
    ) {
      PlayerAudioVisualization(
        minValueColor = MaterialTheme.colorScheme.inversePrimary,
        maxValueColor = MaterialTheme.colorScheme.primary,
        values = audioData.orEmpty(),
        animationDurationMs = 360,
        modifier = Modifier.fillMaxSize(),
        isPlaying = playerViewState.isPlaying,
      )
    }
  }
}

@Composable
private fun rememberThemeSeedColor(
  playerViewState: PlayerViewState,
  applicationComponent: ApplicationComponent,
): Color {
  val fallbackSeedColor = rememberThemeInfo().seedColor
  val currentTrackImageBitmap by
    produceState<ImageBitmap?>(initialValue = null, key1 = playerViewState.currentTrackArtworkUrl) {
      when (playerViewState) {
        is PlayerViewState.Invisible -> {
          value = null
        }
        is PlayerViewState.Playback -> {
          value =
            playerViewState.currentTrack?.artworkUrl?.let {
              applicationComponent.imageLoader.loadImageBitmapOrNull(
                url = it,
                platformContext = applicationComponent.coilPlatformContext,
              )
            }
        }
        is PlayerViewState.Loading,
        is PlayerViewState.Error -> {}
      }
    }
  return currentTrackImageBitmap?.let { rememberThemeColor(it, fallbackSeedColor) }
    ?: fallbackSeedColor
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun AppBottomSheetScaffold(
  appLayoutState: AppLayoutState,
  playerViewState: PlayerViewState,
  currentPlaylist: Playlist?,
  navController: NavHostController,
  applicationComponent: ApplicationComponent,
) {
  val scope = rememberCoroutineScope()

  val paneNavigator =
    rememberSupportingPaneScaffoldNavigator(
      scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo())
    )
  val supportingPaneValue = paneNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
  LaunchedEffect(supportingPaneValue) {
    scope.launch { appLayoutState.onSupportingPaneValueChange(supportingPaneValue) }
  }

  val safeDrawingPaddingValues = WindowInsets.safeDrawing.asPaddingValues()
  val density = LocalDensity.current

  val sheetOffset = appLayoutState.playerLayoutState.currentSheetOffset
  val sheetPeekHeight = 128.dp
  var sheetHeightPx by remember { mutableFloatStateOf(0f) }

  val transitionProgress =
    remember(sheetOffset, sheetHeightPx) {
      if (sheetHeightPx > 0f) (sheetOffset / sheetHeightPx).coerceIn(0f, 1f) else 0f
    }

  val transitionThreshold = 0.5f
  val thresholdProgress =
    remember(transitionProgress) {
      ((transitionProgress - transitionThreshold) / (1f - transitionThreshold)).coerceIn(0f, 1f)
    }

  val expandedAlpha = remember(thresholdProgress) { 1f - thresholdProgress }.coerceIn(0f, 1f)
  val partiallyExpandedAlpha = remember(thresholdProgress) { thresholdProgress }.coerceIn(0f, 1f)

  val hazeState = remember(::HazeState)
  val sheetHazeStyle =
    HazeStyle(
      backgroundColor = BottomSheetDefaults.ContainerColor,
      tint =
        HazeTint(
          BottomSheetDefaults.ContainerColor.copy(
            alpha = GRADIENT_BASE_ALPHA + expandedAlpha * (GRADIENT_MAX_ALPHA - GRADIENT_BASE_ALPHA)
          )
        ),
    )

  BottomSheetScaffold(
    sheetContent = {
      PlayerSheetContent(
        viewState = playerViewState,
        currentPlaylist = currentPlaylist,
        partiallyExpandedAlpha = partiallyExpandedAlpha,
        expandedAlpha = expandedAlpha,
        showToggleFavourite = currentWindowHeightClass() != WindowHeightSizeClass.Compact,
        modifier =
          Modifier.fillMaxSize()
            .defaultHazeEffect(hazeState, sheetHazeStyle)
            .onGloballyPositioned { layoutCoordinates ->
              sheetHeightPx =
                layoutCoordinates.size.height.toFloat() - with(density) { sheetPeekHeight.toPx() }
            },
      )
    },
    sheetDragHandle = {
      Column(modifier = Modifier.fillMaxWidth().defaultHazeEffect(hazeState, sheetHazeStyle)) {
        Spacer(
          modifier = Modifier.height(safeDrawingPaddingValues.calculateTopPadding() * expandedAlpha)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
          BottomSheetDefaults.DragHandle(modifier = Modifier.align(Alignment.Center))

          PlayerCancelPlaybackButton(
            modifier =
              Modifier.align(Alignment.CenterEnd)
                .alpha(partiallyExpandedAlpha)
                .padding(end = Spacing.medium16dp),
            enabled = partiallyExpandedAlpha == 1f,
            onClick = playerViewState.cancelPlayback,
          )
        }
      }
    },
    sheetPeekHeight = sheetPeekHeight,
    scaffoldState = appLayoutState.playerLayoutState.scaffoldState,
  ) {
    SupportingPaneScaffold(
      directive = paneNavigator.scaffoldDirective,
      value = paneNavigator.scaffoldValue,
      mainPane = {
        AnimatedPane {
          AppNavHost(
            applicationComponent = applicationComponent,
            navController = navController,
            hazeState = hazeState,
            bottomSpacerHeight =
              if (
                supportingPaneValue == PaneAdaptedValue.Expanded ||
                  appLayoutState.playerLayoutState.currentSheetValue == SheetValue.Hidden
              ) {
                Spacing.medium16dp
              } else {
                sheetPeekHeight
              },
            showFABs = !playerViewState.playerVisible,
            onCarryOnPlaylistClick = playerViewState.startCarryOnPlaylistPlayback,
            onPlaylistClick = playerViewState.startPlaylistPlayback,
          )
        }
      },
      supportingPane = {
        AnimatedPane {
          PlayerExpandedContent(
            viewState = playerViewState,
            currentPlaylist = currentPlaylist,
            showEdgeGradients = true,
            modifier =
              Modifier.fillMaxSize()
                .padding(
                  top =
                    with(density) { TopAppBarDefaults.windowInsets.getTop(density).toDp() } +
                      Spacing.medium16dp,
                  bottom = Spacing.medium16dp,
                ),
          )
        }
      },
    )
  }
}

@Composable
private fun AppBottomNavigationBar(
  currentDestination: NavDestination?,
  onNavigateToRoute: (AppRoute) -> Unit,
) {
  NavigationBar(modifier = Modifier.fillMaxWidth()) {
    APP_ROUTES.forEach { route ->
      NavigationBarItem(
        selected = route.isSelected(currentDestination),
        onClick = { onNavigateToRoute(route) },
        icon = {
          Icon(imageVector = route.icon, contentDescription = stringResource(route.labelResource))
        },
        label = { Text(stringResource(route.labelResource)) },
      )
    }
  }
}

@Composable
private fun AppNavigationRail(
  currentDestination: NavDestination?,
  navigationContentPosition: NavigationContentPosition,
  onNavigateToRoute: (AppRoute) -> Unit,
) {
  NavigationRail(modifier = Modifier.fillMaxHeight()) {
    val paddingValues = WindowInsets.safeDrawing.asPaddingValues()
    Column(
      modifier =
        Modifier.padding(
          top = paddingValues.calculateTopPadding(),
          bottom = paddingValues.calculateBottomPadding(),
          start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
        ),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall4dp),
    ) {
      if (navigationContentPosition == NavigationContentPosition.CENTER) {
        Spacer(modifier = Modifier.weight(1f))
      }

      APP_ROUTES.forEach { route ->
        NavigationRailItem(
          selected = route.isSelected(currentDestination),
          onClick = { onNavigateToRoute(route) },
          icon = {
            Icon(imageVector = route.icon, contentDescription = stringResource(route.labelResource))
          },
          label = { Text(stringResource(route.labelResource)) },
        )
      }

      if (navigationContentPosition == NavigationContentPosition.CENTER) {
        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun AppPermanentNavigationDrawer(
  currentDestination: NavDestination?,
  navigationContentPosition: NavigationContentPosition,
  onNavigateToRoute: (AppRoute) -> Unit,
) {
  PermanentDrawerSheet(modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp)) {
    val paddingValues = WindowInsets.safeDrawing.asPaddingValues()
    Column(
      modifier =
        Modifier.verticalScroll(rememberScrollState())
          .padding(
            top = paddingValues.calculateTopPadding() + Spacing.medium16dp,
            bottom = paddingValues.calculateBottomPadding() + Spacing.medium16dp,
            start =
              paddingValues.calculateStartPadding(LocalLayoutDirection.current) +
                Spacing.medium16dp,
            end = Spacing.medium16dp,
          ),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      if (navigationContentPosition == NavigationContentPosition.CENTER) {
        Spacer(modifier = Modifier.weight(1f))
      }

      APP_ROUTES.forEach { route ->
        NavigationDrawerItem(
          selected = route.isSelected(currentDestination),
          label = {
            Text(
              text = stringResource(route.labelResource),
              modifier = Modifier.padding(horizontal = Spacing.medium16dp),
            )
          },
          icon = {
            Icon(imageVector = route.icon, contentDescription = stringResource(route.labelResource))
          },
          colors =
            NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
          onClick = { onNavigateToRoute(route) },
        )
      }

      if (navigationContentPosition == NavigationContentPosition.CENTER) {
        Spacer(modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun AppNavHost(
  applicationComponent: ApplicationComponent,
  navController: NavHostController,
  hazeState: HazeState,
  bottomSpacerHeight: Dp,
  showFABs: Boolean,
  onCarryOnPlaylistClick: (CarryOnPlaylist) -> Unit,
  onPlaylistClick: (Playlist) -> Unit,
) {
  SharedTransitionLayout {
    NavHost(
      modifier = Modifier.fillMaxSize(),
      navController = navController,
      startDestination = AppRoute.DiscoverGraph,
    ) {
      navigation<AppRoute.DiscoverGraph>(startDestination = DiscoverGraphRoute.DiscoverPage) {
        @Composable
        fun discoverViewModel(): DiscoverViewModel =
          viewModel(
            factory = applicationComponent.discoverViewModelFactory,
            viewModelStoreOwner = navController.getBackStackEntry<AppRoute.DiscoverGraph>(),
          )

        fun navigateToDiscoverGraphRoute(route: DiscoverGraphRoute) {
          navController.navigate(route) { launchSingleTop = true }
        }

        composable<DiscoverGraphRoute.DiscoverPage> {
          DiscoverPage(
            viewModel = discoverViewModel(),
            animatedContentScope = this@composable,
            hazeState = hazeState,
            bottomSpacerHeight = bottomSpacerHeight,
            showPlayRandomFAB = showFABs,
            onCarryOnPlaylistClick = onCarryOnPlaylistClick,
            onPlaylistClick = onPlaylistClick,
            onMoodClick = { navigateToDiscoverGraphRoute(DiscoverGraphRoute.MoodPage(it)) },
            onViewAllCarryOnPlaylistsClick = {
              navigateToDiscoverGraphRoute(DiscoverGraphRoute.CarryOnPlaylistsPage)
            },
            onViewAllMoodsClick = { navigateToDiscoverGraphRoute(DiscoverGraphRoute.MoodsPage) },
            onViewAllFavouritePlaylistsClick = {
              navigateToDiscoverGraphRoute(DiscoverGraphRoute.FavouritePlaylistsPage)
            },
            onViewAllTrendingPlaylistsClick = {
              navigateToDiscoverGraphRoute(DiscoverGraphRoute.TrendingPlaylistsPage)
            },
          )
        }

        composable<DiscoverGraphRoute.CarryOnPlaylistsPage> {
          CarryOnPlaylistsPage(
            playlists = discoverViewModel().carryOnPlaylists.collectAsStateWithLifecycle().value,
            animatedContentScope = this@composable,
            hazeState = hazeState,
            bottomSpacerHeight = bottomSpacerHeight,
            showFABs = showFABs,
            onPlaylistClick = onCarryOnPlaylistClick,
            onNavigationIconClick = navController::popBackStack,
          )
        }

        composable<DiscoverGraphRoute.MoodPage> {
          MoodPage(
            viewModel =
              MoodViewModel(
                mood = it.toRoute<DiscoverGraphRoute.MoodPage>().mood,
                playlistsRepository = applicationComponent.playlistsRepository,
              ),
            hazeState = hazeState,
            bottomSpacerHeight = bottomSpacerHeight,
            showFABs = showFABs,
            onPlaylistClick = onPlaylistClick,
            onNavigationIconClick = navController::popBackStack,
          )
        }

        composable<DiscoverGraphRoute.MoodsPage> {
          MoodsPage(
            animatedContentScope = this@composable,
            hazeState = hazeState,
            bottomSpacerHeight = bottomSpacerHeight,
            onMoodClick = { navigateToDiscoverGraphRoute(DiscoverGraphRoute.MoodPage(it)) },
            onNavigationIconClick = navController::popBackStack,
          )
        }

        composable<DiscoverGraphRoute.FavouritePlaylistsPage> {
          PlaylistsPage(
            playlists = discoverViewModel().favouritePlaylists.collectAsStateWithLifecycle().value,
            title = stringResource(Res.string.favourite),
            animatedContentScope = this@composable,
            hazeState = hazeState,
            bottomSpacerHeight = bottomSpacerHeight,
            sharedElementKeyPrefix = stringResource(Res.string.favourite),
            showFABs = showFABs,
            onPlaylistClick = onPlaylistClick,
            onNavigationIconClick = navController::popBackStack,
            onRetryClick = {},
          )
        }

        composable<DiscoverGraphRoute.TrendingPlaylistsPage> {
          val discoverViewModel = discoverViewModel()
          PlaylistsPage(
            playlists = discoverViewModel.trendingPlaylists.collectAsStateWithLifecycle().value,
            title = stringResource(Res.string.trending),
            animatedContentScope = this@composable,
            hazeState = hazeState,
            bottomSpacerHeight = bottomSpacerHeight,
            sharedElementKeyPrefix = stringResource(Res.string.trending),
            showFABs = showFABs,
            onPlaylistClick = onPlaylistClick,
            onNavigationIconClick = navController::popBackStack,
            onRetryClick = discoverViewModel.trendingPlaylists::restart,
          )
        }
      }

      composable<AppRoute.SearchPage> {
        SearchPage(
          viewModel = viewModel(factory = applicationComponent.searchViewModelFactory),
          hazeState = hazeState,
          bottomSpacerHeight = bottomSpacerHeight,
          showFABs = showFABs,
          onPlaylistClick = onPlaylistClick,
        )
      }
    }
  }
}

private fun NavController.navigateToAppRoute(route: AppRoute) {
  navigate(route) {
    popUpTo(graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
  }
}
