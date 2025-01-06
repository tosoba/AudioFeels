package com.trm.audiofeels.core.base.model

import kotlinx.coroutines.flow.StateFlow

expect class ArgumentHandle<T> {
  val flow: StateFlow<T?>

  var value: T?
}
