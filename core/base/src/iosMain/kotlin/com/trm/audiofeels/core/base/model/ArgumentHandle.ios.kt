package com.trm.audiofeels.core.base.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class ArgumentPlatformHandle<T>(initialValue: T? = null) : ArgumentHandle<T> {
  private val _flow = MutableStateFlow(initialValue)
  override val flow: StateFlow<T?> = _flow.asStateFlow()

  override var value: T?
    get() = flow.value
    set(value) {
      _flow.value = value
    }
}
