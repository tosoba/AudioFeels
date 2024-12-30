package com.trm.audiofeels.core.base.util

actual abstract class PlatformContext private constructor() {
  companion object {
    val INSTANCE = object : PlatformContext() {}
  }
}
