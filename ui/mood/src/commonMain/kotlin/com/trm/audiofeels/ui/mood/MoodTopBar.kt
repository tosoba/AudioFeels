package com.trm.audiofeels.ui.mood

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trm.audiofeels.core.ui.compose.theme.topAppBarColorsWithGradient
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.go_back
import com.trm.audiofeels.domain.model.Mood
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoodTopBar(mood: Mood, hazeState: HazeState, onNavigationIconClick: () -> Unit) {
  val hazeStyle = HazeStyle(backgroundColor = MaterialTheme.colorScheme.background, tint = null)
  TopAppBar(
    title = {
      Text(
        text = mood.name,
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
    actions = {
      Text(
        text = mood.symbol,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(end = 16.dp),
      )
    },
    colors = topAppBarColorsWithGradient(),
    modifier =
      Modifier.hazeEffect(hazeState) {
        style = hazeStyle
        blurRadius = 10.dp
      },
  )
}
