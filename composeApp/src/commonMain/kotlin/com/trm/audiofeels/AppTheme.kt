package com.trm.audiofeels

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

val primaryLight = Color(0xFF7C580D)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFDEAA)
val onPrimaryContainerLight = Color(0xFF5F4100)
val secondaryLight = Color(0xFF6D5C3F)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFF8DFBB)
val onSecondaryContainerLight = Color(0xFF54442A)
val tertiaryLight = Color(0xFF4E6543)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFD0EBC0)
val onTertiaryContainerLight = Color(0xFF364D2D)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFFFF8F3)
val onBackgroundLight = Color(0xFF201B13)
val surfaceLight = Color(0xFFFFF8F3)
val onSurfaceLight = Color(0xFF201B13)
val surfaceVariantLight = Color(0xFFEEE0CF)
val onSurfaceVariantLight = Color(0xFF4E4539)
val outlineLight = Color(0xFF807667)
val outlineVariantLight = Color(0xFFD2C5B4)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF353027)
val inverseOnSurfaceLight = Color(0xFFFBEFE2)
val inversePrimaryLight = Color(0xFFEFBF6D)
val surfaceDimLight = Color(0xFFE3D8CC)
val surfaceBrightLight = Color(0xFFFFF8F3)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFDF2E5)
val surfaceContainerLight = Color(0xFFF8ECDF)
val surfaceContainerHighLight = Color(0xFFF2E6D9)
val surfaceContainerHighestLight = Color(0xFFECE1D4)

val primaryDark = Color(0xFFEFBF6D)
val onPrimaryDark = Color(0xFF422C00)
val primaryContainerDark = Color(0xFF5F4100)
val onPrimaryContainerDark = Color(0xFFFFDEAA)
val secondaryDark = Color(0xFFDBC3A1)
val onSecondaryDark = Color(0xFF3C2E16)
val secondaryContainerDark = Color(0xFF54442A)
val onSecondaryContainerDark = Color(0xFFF8DFBB)
val tertiaryDark = Color(0xFFB4CEA5)
val onTertiaryDark = Color(0xFF213619)
val tertiaryContainerDark = Color(0xFF364D2D)
val onTertiaryContainerDark = Color(0xFFD0EBC0)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF17130B)
val onBackgroundDark = Color(0xFFECE1D4)
val surfaceDark = Color(0xFF17130B)
val onSurfaceDark = Color(0xFFECE1D4)
val surfaceVariantDark = Color(0xFF4E4539)
val onSurfaceVariantDark = Color(0xFFD2C5B4)
val outlineDark = Color(0xFF9A8F80)
val outlineVariantDark = Color(0xFF4E4539)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFECE1D4)
val inverseOnSurfaceDark = Color(0xFF353027)
val inversePrimaryDark = Color(0xFF7C580D)
val surfaceDimDark = Color(0xFF17130B)
val surfaceBrightDark = Color(0xFF3F382F)
val surfaceContainerLowestDark = Color(0xFF120E07)
val surfaceContainerLowDark = Color(0xFF201B13)
val surfaceContainerDark = Color(0xFF241F17)
val surfaceContainerHighDark = Color(0xFF2F2921)
val surfaceContainerHighestDark = Color(0xFF3A342B)

private val lightScheme =
  lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
  )

private val darkScheme =
  darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
  )

@Composable
fun AppTheme(
  playerViewState: PlayerViewState,
  applicationComponent: ApplicationComponent,
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) darkScheme else lightScheme,
    typography = audioFeelsTypography(),
  ) {
    val seedColor = rememberThemeSeedColor(playerViewState, applicationComponent)
    DynamicMaterialTheme(
      seedColor = seedColor ?: MaterialTheme.colorScheme.primary,
      primary = if (seedColor == null) MaterialTheme.colorScheme.primary else null,
      secondary = if (seedColor == null) MaterialTheme.colorScheme.secondary else null,
      tertiary = if (seedColor == null) MaterialTheme.colorScheme.tertiary else null,
      error = if (seedColor == null) MaterialTheme.colorScheme.error else null,
      typography = audioFeelsTypography(),
      content = content,
    )
  }
}

@Composable
private fun rememberThemeSeedColor(
  playerViewState: PlayerViewState,
  applicationComponent: ApplicationComponent,
): Color? {
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
  return currentTrackImageBitmap?.let { rememberThemeColor(it) }
}
