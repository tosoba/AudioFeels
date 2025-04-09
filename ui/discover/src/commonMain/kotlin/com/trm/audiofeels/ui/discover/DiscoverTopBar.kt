package com.trm.audiofeels.ui.discover

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.trm.audiofeels.core.ui.compose.theme.topAppBarColorsWithGradient
import com.trm.audiofeels.core.ui.compose.util.defaultHazeEffect
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.app_name
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiscoverTopBar(hazeState: HazeState) {
  TopAppBar(
    title = {
      Text(
        text = stringResource(Res.string.app_name),
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      )
    },
    colors = topAppBarColorsWithGradient(),
    modifier =
      Modifier.defaultHazeEffect(
        hazeState = hazeState,
        hazeStyle = HazeStyle(backgroundColor = MaterialTheme.colorScheme.background, tint = null),
      ),
  )
}
