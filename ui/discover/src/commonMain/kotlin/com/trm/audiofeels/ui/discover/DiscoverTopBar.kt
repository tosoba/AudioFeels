package com.trm.audiofeels.ui.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.compose.theme.Spacing
import com.trm.audiofeels.core.ui.compose.theme.topAppBarColorsWithGradient
import com.trm.audiofeels.core.ui.compose.util.defaultHazeEffect
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.app_name
import com.trm.audiofeels.core.ui.resources.ic_launcher
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import org.jetbrains.compose.resources.painterResource
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
    navigationIcon = {
      Image(
        painter = painterResource(Res.drawable.ic_launcher),
        contentDescription = null,
        modifier = Modifier.size(48.dp).padding(Spacing.small8dp),
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
