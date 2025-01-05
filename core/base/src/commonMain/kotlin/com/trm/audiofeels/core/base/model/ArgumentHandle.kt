package com.trm.audiofeels.core.base.model

import kotlinx.coroutines.flow.StateFlow

interface ArgumentHandle<T> {
  val flow: StateFlow<T?>

  var value: T?
}

expect class ArgumentPlatformHandle<T> : ArgumentHandle<T>
