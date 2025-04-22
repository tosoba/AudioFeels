package com.trm.audiofeels.core.base.model

class InputAction<T>(input: T, private val action: (T) -> Unit) :
  BaseInputAction<T>(input), () -> Unit {
  override fun invoke() {
    action(input)
  }
}

class ParameterizedInputAction<T, S>(input: T, private val action: (T, S) -> Unit) :
  BaseInputAction<T>(input), (S) -> Unit {
  override fun invoke(param: S) {
    action(input, param)
  }
}

abstract class BaseInputAction<T>(protected val input: T) {
  override fun equals(other: Any?): Boolean {
    when {
      this === other -> return true
      other == null || this::class != other::class -> return false
      else -> {
        other as BaseInputAction<*>
        return input == other.input
      }
    }
  }

  override fun hashCode(): Int = input.hashCode()
}
