package com.trm.audiofeels.ui.playlists

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.go_back
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.PlaylistsTopBar(
  title: String,
  hazeState: HazeState,
  onNavigationIconClick: () -> Unit,
) {
  val hazeStyle = HazeStyle(backgroundColor = MaterialTheme.colorScheme.background, tint = null)
  TopAppBar(
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      )
    },
    navigationIcon = {
      IconButton(onClick = onNavigationIconClick) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(Res.string.go_back),
        )
      }
    },
    colors =
      TopAppBarDefaults.topAppBarColors().run {
        copy(containerColor = containerColor.copy(alpha = .85f))
      },
    modifier =
      Modifier.renderInSharedTransitionScopeOverlay().hazeEffect(hazeState) {
        style = hazeStyle
        blurRadius = 10.dp
      },
  )
}
