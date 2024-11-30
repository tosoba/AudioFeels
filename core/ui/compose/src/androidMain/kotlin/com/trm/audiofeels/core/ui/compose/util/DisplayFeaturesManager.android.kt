package com.trm.audiofeels.core.ui.compose.util

import android.app.Activity
import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.window.layout.FoldingFeature
import com.google.accompanist.adaptive.calculateDisplayFeatures
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

actual class DisplayFeaturesPlatformManager(private val activity: Activity) :
  DisplayFeaturesManager {
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
