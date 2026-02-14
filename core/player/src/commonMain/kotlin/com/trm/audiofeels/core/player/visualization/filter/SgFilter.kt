package com.trm.audiofeels.core.player.visualization.filter

import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

class SgFilter(
  private val windowSize: Int,
  private val derivative: Int = 0,
  private val polynomial: Int = 3,
) {
  private val weights: Array<FloatArray>

  init {
    require(!(windowSize % 2 == 0 || windowSize < 5)) {
      "options.WindowSize [$windowSize] must be odd and equal to or greater than 5"
    }
    require(derivative >= 0) { "options.Derivative [$derivative] must be equal or greater than 0" }
    require(polynomial >= 0) { "options.Polynomial [$polynomial] must be equal or greater than 0" }
    this.weights = SgFilters.computeWeights(windowSize, polynomial, derivative)
  }

  fun getHs(h: FloatArray, center: Int, half: Int, derivative: Int): Float {
    var hs = 0f
    var count = 0
    for (i in center - half until center + half) {
      if (i >= 0 && i < h.size - 1) {
        hs += h[i + 1] - h[i]
        count++
      }
    }
    return (hs / count).pow(derivative)
  }

  fun process(data: FloatArray, h: FloatArray, out: FloatArray) {
    require(windowSize <= data.size) {
      "data length [${data.size}] must be larger than options.WindowSize[$windowSize]"
    }

    require(data.size == out.size) { "data/output size mismatch [${data.size}, ${out.size}]" }

    val halfWindow = floor(windowSize / 2.0).roundToInt()
    val numPoints = data.size
    var hs: Float

    for (i in 0 until halfWindow) {
      val wg1 = weights[halfWindow - i - 1]
      val wg2 = weights[halfWindow + i + 1]
      var d1 = 0f
      var d2 = 0f
      for (l in 0 until windowSize) {
        d1 += wg1[l] * data[l]
        d2 += wg2[l] * data[numPoints - windowSize + l]
      }
      hs = getHs(h, halfWindow - i - 1, halfWindow, derivative)
      out[halfWindow - i - 1] = d1 / hs
      hs = getHs(h, numPoints - halfWindow + i, halfWindow, derivative)
      out[numPoints - halfWindow + i] = d2 / hs
    }

    val wg = weights[halfWindow]
    for (i in windowSize..numPoints) {
      var d = 0f
      for (l in 0 until windowSize) {
        d += wg[l] * data[l + i - windowSize]
      }
      hs = getHs(h, i - halfWindow - 1, halfWindow, derivative)
      out[i - halfWindow - 1] = d / hs
    }
  }
}
