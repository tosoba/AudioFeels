package com.trm.audiofeels.core.base.util

import kotlin.math.pow
import kotlin.math.round

fun Double.roundTo(decimals: Int): Double {
  require(decimals >= 0) { "Decimals must be non-negative" }
  val multiplier = 10.0.pow(decimals)
  return round(this * multiplier) / multiplier
}
