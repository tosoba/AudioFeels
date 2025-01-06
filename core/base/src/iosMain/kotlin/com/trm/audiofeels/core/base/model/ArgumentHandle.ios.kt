package com.trm.audiofeels.core.base.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class ArgumentHandle<T>(initialValue: T? = null) {
  private val _flow = MutableStateFlow(initialValue)
  actual val flow: StateFlow<T?> = _flow.asStateFlow()

  actual var value: T?
    get() = flow.value
    set(value) {
      _flow.value = value
    }
}
