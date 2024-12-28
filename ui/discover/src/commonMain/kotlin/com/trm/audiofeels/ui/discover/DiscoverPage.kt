package com.trm.audiofeels.ui.discover

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trm.audiofeels.core.base.model.LoadableState

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DiscoverPage(
  modifier: Modifier = Modifier,
  viewModel: DiscoverViewModel,
  onPlayClick: () -> Unit,
  showSupportingPane: Boolean,
  onSupportingPaneValueChange: (PaneAdaptedValue) -> Unit,
  supportingPaneContent: @Composable AnimatedPaneScope.() -> Unit,
) {
  val navigator =
    rememberSupportingPaneScaffoldNavigator(
      scaffoldDirective =
        calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()).let {
          if (showSupportingPane) it
          else it.copy(maxHorizontalPartitions = 1, maxVerticalPartitions = 1)
        }
    )
  val supportingPaneValue = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
  LaunchedEffect(supportingPaneValue) { onSupportingPaneValueChange(supportingPaneValue) }

  SupportingPaneScaffold(
    modifier = modifier,
    directive = navigator.scaffoldDirective,
    value = navigator.scaffoldValue,
    mainPane = {
      AnimatedPane(modifier = Modifier.safeContentPadding()) {
        val playlists by viewModel.playlists.collectAsStateWithLifecycle()
        Crossfade(playlists) {
          when (it) {
            LoadableState.Loading -> {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
              }
            }
            is LoadableState.Success -> {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(all = 12.dp),
              ) {
                items(it.value) { playlist -> Text(playlist.name) }
              }
            }
            is LoadableState.Error -> {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Button(onClick = viewModel::loadPlaylists) { Text("Retry") }
              }
            }
          }
        }
      }
    },
    supportingPane = {
      AnimatedPane(modifier = Modifier.safeContentPadding(), content = supportingPaneContent)
    },
  )
}
