package com.trm.audiofeels

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.ktx.rememberThemeColor
import com.trm.audiofeels.core.ui.compose.theme.audioFeelsTypography
import com.trm.audiofeels.core.ui.compose.util.loadImageBitmapOrNull
import com.trm.audiofeels.di.ApplicationComponent
import com.trm.audiofeels.ui.player.PlayerViewState
import com.trm.audiofeels.ui.player.util.currentTrackArtworkUrl

@Composable
fun AppTheme(
  playerViewState: PlayerViewState,
  applicationComponent: ApplicationComponent,
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  DynamicMaterialTheme(
    seedColor = rememberThemeSeedColor(playerViewState, applicationComponent),
    typography = audioFeelsTypography(),
    isDark = darkTheme,
    content = content,
  )
}

@Composable
private fun rememberThemeSeedColor(
  playerViewState: PlayerViewState,
  applicationComponent: ApplicationComponent,
): Color {
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
  return currentTrackImageBitmap?.let { rememberThemeColor(it) } ?: fallbackSeedColor
}

private val fallbackSeedColor = Color(0xFFE7870A)
