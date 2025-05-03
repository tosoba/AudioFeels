package com.trm.audiofeels.core.player.visualizer.filter

import kotlin.math.floor
import kotlin.math.roundToInt

internal object SgFilters {
  private fun gramPolynomial(i: Int, m: Int, k: Int, s: Int): Float =
    if (k > 0) {
      val t0 = (4 * k - 2).toFloat()
      val t1 = (k * (2 * m - k + 1)).toFloat()
      val t2 = t0 / t1

      val t3 = i * gramPolynomial(i, m, k - 1, s)
      val t4 = s * gramPolynomial(i, m, k - 1, s - 1)

      val t5 = ((k - 1) * (2 * m + k)).toFloat()
      val t6 = (k * (2 * m - k + 1)).toFloat()
      val t7 = t5 / t6

      val t8 = gramPolynomial(i, m, k - 2, s)

      t2 * (t3 + t4) - t7 * t8
    } else {
      if (k == 0 && s == 0) {
        1f
      } else {
        0f
      }
    }

  private fun productOfRange(a: Int, b: Int): Int {
    var gf = 1
    if (a >= b) {
      for (j in a - b + 1..a) {
        gf *= j
      }
    }
    return gf
  }

  private fun polyWeight(
    i: Int,
    t: Int,
    windowMiddle: Int,
    polynomial: Int,
    derivative: Int,
  ): Float {
    var sum = 0f
    for (k in 0..polynomial) {
      val t0 = (2 * k + 1).toFloat()
      val t1 = productOfRange(2 * windowMiddle, k).toFloat()
      val t2 = productOfRange(2 * windowMiddle + k + 1, k + 1).toFloat()
      val t3 = gramPolynomial(i, windowMiddle, k, 0)
      val t4 = gramPolynomial(t, windowMiddle, k, derivative)
      sum += t0 * (t1 / t2) * t3 * t4
    }
    return sum
  }

  fun computeWeights(windowSize: Int, polynomial: Int, derivative: Int): Array<FloatArray> {
    val weights = Array(windowSize) { floatArrayOf() }
    val windowMiddle = floor(windowSize / 2.0).roundToInt()

    for (row in -windowMiddle..windowMiddle) {
      weights[row + windowMiddle] = FloatArray(windowSize)
      for (col in -windowMiddle..windowMiddle) {
        weights[row + windowMiddle][col + windowMiddle] =
          polyWeight(col, row, windowMiddle, polynomial, derivative)
      }
    }

    return weights
  }
}
