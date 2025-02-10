package com.trm.audiofeels.core.base.model

import io.github.aakira.napier.Napier
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

sealed interface LoadableState<out T> {
  data object Loading : LoadableState<Nothing>

  data class Idle<T>(val value: T) : LoadableState<T>

  data class Error(val throwable: Throwable?) : LoadableState<Nothing>

  val valueOrNull: T?
    get() = if (this is Idle) value else null
}

fun <T, R> LoadableState<T>.map(mapper: (T) -> R): LoadableState<R> =
  when (this) {
    LoadableState.Loading -> LoadableState.Loading
    is LoadableState.Idle -> LoadableState.Idle(mapper(value))
    is LoadableState.Error -> LoadableState.Error(throwable)
  }

fun <T> loadableStateFlowOf(
  timeout: Duration = DefaultTimeout,
  load: suspend () -> T,
): Flow<LoadableState<T>> =
  flow {
      emit(LoadableState.Loading)
      emit(LoadableState.Idle(withTimeout(timeout) { load() }))
    }
    .catch {
      Napier.e(message = "LoadableStateError", throwable = it)
      emit(LoadableState.Error(it))
    }

internal val DefaultTimeout = 20.seconds
