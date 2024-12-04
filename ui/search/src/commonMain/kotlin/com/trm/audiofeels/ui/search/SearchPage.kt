package com.trm.audiofeels.ui.search

import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.search
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SearchPage(modifier: Modifier = Modifier, onPlayerPaneValueChange: (PaneAdaptedValue) -> Unit) {
  val navigator = rememberSupportingPaneScaffoldNavigator()

  val playerPaneValue = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
  LaunchedEffect(playerPaneValue) { onPlayerPaneValueChange(playerPaneValue) }

  SupportingPaneScaffold(
    modifier = modifier,
    directive = navigator.scaffoldDirective,
    value = navigator.scaffoldValue,
    mainPane = {
      AnimatedPane(modifier = Modifier.safeContentPadding()) {
        Text(stringResource(Res.string.search))
      }
    },
    supportingPane = {
      AnimatedPane(modifier = Modifier.safeContentPadding()) {
        Text(stringResource(Res.string.search))
      }
    },
  )
}
