package com.trm.audiofeels.core.base.model

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow

actual class ArgumentPlatformHandle<T>(
  private val savedStateHandle: SavedStateHandle,
  private val key: String,
  private val initialValue: T? = null,
) : ArgumentHandle<T> {
  override val flow: StateFlow<T?>
    get() = savedStateHandle.getStateFlow(key, initialValue)

  override var value: T?
    get() = savedStateHandle[key]
    set(value) {
      savedStateHandle[key] = value
    }

  override fun clear() {
    savedStateHandle.remove<T>(key)
  }
}
