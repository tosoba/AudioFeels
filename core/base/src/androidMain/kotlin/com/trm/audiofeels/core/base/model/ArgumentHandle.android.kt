package com.trm.audiofeels.core.base.model

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow

actual class ArgumentHandle<T>(
  private val savedStateHandle: SavedStateHandle,
  private val key: String,
  private val initialValue: T? = null,
) {
  actual val flow: StateFlow<T?>
    get() = savedStateHandle.getStateFlow(key, initialValue)

  actual var value: T?
    get() = savedStateHandle[key]
    set(value) {
      savedStateHandle[key] = value
    }
}
