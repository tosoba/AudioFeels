package com.trm.audiofeels.core.ui.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import com.trm.audiofeels.core.ui.resources.FunnelDisplayFontFamily
import com.trm.audiofeels.core.ui.resources.FunnelSansFontFamily

private val base = Typography()

@Composable
fun audioFeelsTypography(): Typography =
  Typography(
    displayLarge = base.displayLarge.copy(fontFamily = FunnelDisplayFontFamily),
    displayMedium = base.displayMedium.copy(fontFamily = FunnelDisplayFontFamily),
    displaySmall = base.displaySmall.copy(fontFamily = FunnelDisplayFontFamily),
    headlineLarge = base.headlineLarge.copy(fontFamily = FunnelSansFontFamily),
    headlineMedium = base.headlineMedium.copy(fontFamily = FunnelSansFontFamily),
    headlineSmall = base.headlineSmall.copy(fontFamily = FunnelSansFontFamily),
    titleLarge = base.titleLarge.copy(fontFamily = FunnelSansFontFamily),
    titleMedium = base.titleMedium.copy(fontFamily = FunnelSansFontFamily),
    titleSmall = base.titleSmall.copy(fontFamily = FunnelSansFontFamily),
    bodyLarge = base.bodyLarge.copy(fontFamily = FunnelSansFontFamily),
    bodyMedium = base.bodyMedium.copy(fontFamily = FunnelSansFontFamily),
    bodySmall = base.bodySmall.copy(fontFamily = FunnelSansFontFamily),
    labelLarge = base.labelLarge.copy(fontFamily = FunnelSansFontFamily),
    labelMedium = base.labelMedium.copy(fontFamily = FunnelSansFontFamily),
    labelSmall = base.labelSmall.copy(fontFamily = FunnelSansFontFamily),
  )
