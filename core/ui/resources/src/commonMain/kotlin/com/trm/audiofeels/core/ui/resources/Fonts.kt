package com.trm.audiofeels.core.ui.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font

val FunnelSansFontFamily: FontFamily
  @Composable
  get() =
    FontFamily(
      Font(Res.font.FunnelSans_Light, weight = FontWeight.Light),
      Font(Res.font.FunnelSans_Regular, weight = FontWeight.Normal),
      Font(Res.font.FunnelSans_Medium, weight = FontWeight.Medium),
      Font(Res.font.FunnelSans_SemiBold, weight = FontWeight.SemiBold),
      Font(Res.font.FunnelSans_Bold, weight = FontWeight.Bold),
      Font(Res.font.FunnelSans_ExtraBold, weight = FontWeight.ExtraBold),
    )

val FunnelDisplayFontFamily: FontFamily
  @Composable
  get() =
    FontFamily(
      Font(Res.font.FunnelDisplay_Regular, weight = FontWeight.Normal),
      Font(Res.font.FunnelDisplay_Medium, weight = FontWeight.Medium),
      Font(Res.font.FunnelDisplay_SemiBold, weight = FontWeight.SemiBold),
      Font(Res.font.FunnelDisplay_Bold, weight = FontWeight.Bold),
      Font(Res.font.FunnelDisplay_ExtraBold, weight = FontWeight.ExtraBold),
    )
