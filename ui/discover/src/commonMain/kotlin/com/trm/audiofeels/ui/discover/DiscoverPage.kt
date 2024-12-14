package com.trm.audiofeels.ui.discover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.discover
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DiscoverPage(
  modifier: Modifier = Modifier,
  showSupportingPane: Boolean,
  onSupportingPaneValueChange: (PaneAdaptedValue) -> Unit,
  onPlayClick: () -> Unit,
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
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
          Button(onClick = onPlayClick) { Text("Play") }
        }
      }
    },
    supportingPane = {
      AnimatedPane(modifier = Modifier.safeContentPadding()) {
        Text(stringResource(Res.string.discover))
      }
    },
  )
}
