package com.trm.audiofeels

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowHeightSizeClass
import coil3.compose.setSingletonImageLoaderFactory
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.ktx.rememberThemeColor
import com.trm.audiofeels.core.ui.compose.theme.UpdateEdgeToEdge
import com.trm.audiofeels.core.ui.compose.util.NavigationContentPosition
import com.trm.audiofeels.core.ui.compose.util.NavigationType
import com.trm.audiofeels.core.ui.compose.util.calculateWindowSize
import com.trm.audiofeels.core.ui.compose.util.loadImageBitmapOrNull
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.app_name
import com.trm.audiofeels.di.ApplicationComponent
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.ui.discover.DiscoverPage
import com.trm.audiofeels.ui.player.PlayerViewModel
import com.trm.audiofeels.ui.player.PlayerViewState
import com.trm.audiofeels.ui.player.composable.PlayerAudioVisualization
import com.trm.audiofeels.ui.player.composable.PlayerCancelPlaybackButton
import com.trm.audiofeels.ui.player.composable.PlayerExpandedContent
import com.trm.audiofeels.ui.player.composable.PlayerRecordAudioPermissionObserver
import com.trm.audiofeels.ui.player.composable.PlayerRecordAudioPermissionRequest
import com.trm.audiofeels.ui.player.composable.PlayerSheetContent
import com.trm.audiofeels.ui.search.SearchPage
import com.trm.audiofeels.ui.search.SearchTopBar
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.zwander.compose.rememberThemeInfo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun AppContent(applicationComponent: ApplicationComponent) {
  setSingletonImageLoaderFactory { applicationComponent.imageLoader }

  UpdateEdgeToEdge(isSystemInDarkTheme())

  val playerViewModel =
    viewModel<PlayerViewModel>(factory = applicationComponent.playerViewModelFactory)
  val playerViewState by playerViewModel.viewState.collectAsStateWithLifecycle()
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
      NavigationType(adaptiveInfo = adaptiveInfo, windowSize = calculateWindowSize())
    val navigationContentPosition =
      NavigationContentPosition(adaptiveInfo.windowSizeClass.windowHeightSizeClass)

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
                    confirmValueChange = {
                      it != SheetValue.Hidden || !playerViewState.playerVisible
                    },
                    skipHiddenState = false,
                  )
              )
          ),
      )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    fun navigateToPageDestination(destination: AppPageNavigationDestination) {
      scope.launch { appLayoutState.onNavigateToPageDestination() }
      navController.navigateToPageDestination(destination)
    }

    NavigationSuiteScaffoldLayout(
      layoutType = navigationType.suiteType,
      navigationSuite = {
        when (navigationType) {
          NavigationType.NAVIGATION_BAR -> {
            AppBottomNavigationBar(
              currentDestination = currentDestination,
              navigatePageDestination = ::navigateToPageDestination,
            )
          }
          NavigationType.NAVIGATION_RAIL -> {
            AppNavigationRail(
              currentDestination = currentDestination,
              navigationContentPosition = navigationContentPosition,
              navigatePageDestination = ::navigateToPageDestination,
            )
          }
          NavigationType.PERMANENT_NAVIGATION_DRAWER -> {
            AppPermanentNavigationDrawer(
              currentDestination = currentDestination,
              navigationContentPosition = navigationContentPosition,
              navigatePageDestination = ::navigateToPageDestination,
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
      scaffoldDirective =
        calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()).let {
          if (playerViewState.playerVisible) it
          else it.copy(maxHorizontalPartitions = 1, maxVerticalPartitions = 1)
        }
    )
  val supportingPaneValue = paneNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
  LaunchedEffect(supportingPaneValue) {
    scope.launch { appLayoutState.onSupportingPaneValueChange(supportingPaneValue) }
  }

  val safeDrawingPaddingValues = WindowInsets.safeDrawing.asPaddingValues()
  val density = LocalDensity.current

  val sheetOffset = appLayoutState.playerLayoutState.currentSheetOffset
  val sheetPeekHeight = 128.dp
  var sheetHeightPx by remember { mutableStateOf(0f) }

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
      tint = HazeTint(BottomSheetDefaults.ContainerColor.copy(alpha = .85f + expandedAlpha * .1f)),
    )

  BottomSheetScaffold(
    sheetContent = {
      PlayerSheetContent(
        viewState = playerViewState,
        currentPlaylist = currentPlaylist,
        partiallyExpandedAlpha = partiallyExpandedAlpha,
        expandedAlpha = expandedAlpha,
        showToggleFavourite =
          currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass !=
            WindowHeightSizeClass.COMPACT,
        modifier =
          Modifier.fillMaxSize()
            .hazeEffect(hazeState) {
              style = sheetHazeStyle
              blurRadius = 10.dp
            }
            .onGloballyPositioned { layoutCoordinates ->
              sheetHeightPx =
                layoutCoordinates.size.height.toFloat() - with(density) { sheetPeekHeight.toPx() }
            },
      )
    },
    sheetDragHandle = {
      Column(
        modifier =
          Modifier.fillMaxWidth().hazeEffect(hazeState) {
            style = sheetHazeStyle
            blurRadius = 10.dp
          }
      ) {
        Spacer(
          modifier = Modifier.height(safeDrawingPaddingValues.calculateTopPadding() * expandedAlpha)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
          BottomSheetDefaults.DragHandle(modifier = Modifier.align(Alignment.Center))

          PlayerCancelPlaybackButton(
            modifier =
              Modifier.align(Alignment.CenterEnd)
                .alpha(partiallyExpandedAlpha)
                .padding(end = 16.dp),
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
          Box {
            AppNavHost(
              applicationComponent = applicationComponent,
              navController = navController,
              topSpacerHeight =
                with(density) { TopAppBarDefaults.windowInsets.getTop(density).toDp() } +
                  TopAppBarDefaults.TopAppBarExpandedHeight,
              bottomSpacerHeight =
                if (
                  supportingPaneValue == PaneAdaptedValue.Expanded ||
                    appLayoutState.playerLayoutState.currentSheetValue == SheetValue.Hidden
                ) {
                  0.dp
                } else {
                  sheetPeekHeight
                },
              modifier = Modifier.fillMaxSize().hazeSource(hazeState),
              onCarryOnPlaylistClick = playerViewState.startCarryOnPlaylistPlayback,
              onPlaylistClick = playerViewState.startPlaylistPlayback,
            )

            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            TopBar(destination = currentBackStackEntry?.destination, hazeState = hazeState)
          }
        }
      },
      supportingPane = {
        AnimatedPane {
          PlayerExpandedContent(
            viewState = playerViewState,
            currentPlaylist = currentPlaylist,
            showSlider =
              currentWindowAdaptiveInfo().windowSizeClass.windowHeightSizeClass !=
                WindowHeightSizeClass.COMPACT,
            showEdgeGradients = true,
            modifier =
              Modifier.fillMaxSize()
                .padding(
                  top =
                    with(density) { TopAppBarDefaults.windowInsets.getTop(density).toDp() } + 16.dp,
                  bottom = 16.dp,
                ),
          )
        }
      },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(destination: NavDestination?, hazeState: HazeState) {
  val density = LocalDensity.current
  val barHazeStyle = HazeStyle(backgroundColor = MaterialTheme.colorScheme.background, tint = null)

  AnimatedContent(destination) {
    when {
      it?.hasRoute(AppRoute.Search::class) == true -> {
        SearchTopBar(
          modifier =
            Modifier.fillMaxWidth()
              .padding(
                top =
                  with(density) { TopAppBarDefaults.windowInsets.getTop(density).toDp() } + 16.dp,
                start = 16.dp,
                end = 16.dp,
              )
              .clip(SearchBarDefaults.dockedShape)
              .hazeEffect(hazeState) {
                style = barHazeStyle
                blurRadius = 10.dp
              }
        )
      }
      it?.hasRoute(AppRoute.Discover::class) == true -> {
        AppTopBar(
          modifier =
            Modifier.hazeEffect(hazeState) {
              style = barHazeStyle
              blurRadius = 10.dp
            }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(modifier: Modifier = Modifier) {
  val colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
  CenterAlignedTopAppBar(
    title = { Text(stringResource(Res.string.app_name)) },
    colors = colors.copy(containerColor = colors.containerColor.copy(alpha = .85f)),
    modifier = modifier,
  )
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
        label = { Text(stringResource(destination.labelResource)) },
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
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      if (navigationContentPosition == NavigationContentPosition.CENTER) {
        Spacer(modifier = Modifier.weight(1f))
      }

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
          label = { Text(stringResource(destination.labelResource)) },
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
  navigatePageDestination: (AppPageNavigationDestination) -> Unit,
) {
  PermanentDrawerSheet(modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp)) {
    val paddingValues = WindowInsets.safeDrawing.asPaddingValues()
    Column(
      modifier =
        Modifier.verticalScroll(rememberScrollState())
          .padding(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            end = 16.dp,
          ),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      if (navigationContentPosition == NavigationContentPosition.CENTER) {
        Spacer(modifier = Modifier.weight(1f))
      }

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
  topSpacerHeight: Dp,
  bottomSpacerHeight: Dp,
  modifier: Modifier = Modifier,
  onCarryOnPlaylistClick: (CarryOnPlaylist) -> Unit,
  onPlaylistClick: (Playlist) -> Unit,
) {
  NavHost(
    modifier = modifier,
    navController = navController,
    startDestination = AppRoute.Discover,
  ) {
    composable<AppRoute.Discover> {
      DiscoverPage(
        viewModel = viewModel(factory = applicationComponent.discoverViewModelFactory),
        topSpacerHeight = topSpacerHeight,
        bottomSpacerHeight = bottomSpacerHeight,
        onCarryPlaylistClick = onCarryOnPlaylistClick,
        onPlaylistClick = onPlaylistClick,
      )
    }
    composable<AppRoute.Search> {
      SearchPage(
        viewModel = viewModel(factory = applicationComponent.searchViewModelFactory),
        topSpacerHeight = topSpacerHeight,
        bottomSpacerHeight = bottomSpacerHeight,
        onPlaylistClick = onPlaylistClick,
      )
    }
  }
}

private fun NavController.navigateToPageDestination(destination: AppPageNavigationDestination) {
  navigate(destination.route) {
    popUpTo(graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
  }
}
