package com.trm.audiofeels.core.base.model

class ViewStateAction<T>(input: T, private val action: (T) -> Unit) :
  BaseViewStateAction<T>(input), () -> Unit {
  override fun invoke() {
    action(input)
  }
}

class ParameterizedViewStateAction<T, S>(input: T, private val action: (T, S) -> Unit) :
  BaseViewStateAction<T>(input), (S) -> Unit {
  override fun invoke(param: S) {
    action(input, param)
  }
}

abstract class BaseViewStateAction<T>(protected val input: T) {
  override fun equals(other: Any?): Boolean {
    when {
      this === other -> return true
      other == null || this::class != other::class -> return false
      else -> {
        other as BaseViewStateAction<*>
        return input == other.input
      }
    }
  }

  override fun hashCode(): Int = input.hashCode()
}
