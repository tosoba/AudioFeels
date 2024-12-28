package com.trm.audiofeels.ui.favourites

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.favourites
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FavouritesPage(
  modifier: Modifier = Modifier,
  showSupportingPane: Boolean,
  onSupportingPaneValueChange: (PaneAdaptedValue) -> Unit,
  supportingPaneContent: @Composable AnimatedPaneScope.() -> Unit,
) {
  val navigator = rememberSupportingPaneScaffoldNavigator()

  val playerPaneValue = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
  LaunchedEffect(playerPaneValue) { onSupportingPaneValueChange(playerPaneValue) }

  SupportingPaneScaffold(
    modifier = modifier,
    directive = navigator.scaffoldDirective,
    value = navigator.scaffoldValue,
    mainPane = { AnimatedPane { Text(stringResource(Res.string.favourites)) } },
    supportingPane = { AnimatedPane(content = supportingPaneContent) },
  )
}
