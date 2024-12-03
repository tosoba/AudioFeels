package com.trm.audiofeels.core.ui.compose.util

import android.app.Activity
import android.graphics.Rect
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.trm.audiofeels.core.base.util.findActivity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

actual class DisplayPlatformManager(private val activity: Activity) : DisplayManager {
  @Composable
  override fun isNormalDevicePosture(): Boolean {
    val foldingFeature =
      calculateDisplayFeatures(activity).filterIsInstance<FoldingFeature>().firstOrNull()
    return when {
      isBookPosture(foldingFeature) -> {
        DevicePosture.BookPosture(foldingFeature.bounds)
      }
      isSeparating(foldingFeature) -> {
        DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)
      }
      else -> {
        DevicePosture.NormalPosture
      }
    } == DevicePosture.NormalPosture
  }

  @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
  @Composable
  override fun windowSizeClass(): WindowSizeClass = calculateWindowSizeClass(activity)
}

private sealed interface DevicePosture {
  data object NormalPosture : DevicePosture

  data class BookPosture(val hingePosition: Rect) : DevicePosture

  data class Separating(val hingePosition: Rect, var orientation: FoldingFeature.Orientation) :
    DevicePosture
}

@OptIn(ExperimentalContracts::class)
private fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
  contract { returns(true) implies (foldFeature != null) }
  return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
    foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

@OptIn(ExperimentalContracts::class)
private fun isSeparating(foldFeature: FoldingFeature?): Boolean {
  contract { returns(true) implies (foldFeature != null) }
  return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}

@Composable
actual fun rememberDisplayManager(): DisplayManager {
  val activity = LocalContext.current.findActivity()
  return remember { DisplayPlatformManager(activity) }
}
