package com.trm.audiofeels.ui.playlists

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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistsTopBar(
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
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
      }
    },
    colors =
      TopAppBarDefaults.topAppBarColors().run {
        copy(containerColor = containerColor.copy(alpha = .85f))
      },
    modifier =
      Modifier.hazeEffect(hazeState) {
        style = hazeStyle
        blurRadius = 10.dp
      },
  )
}
