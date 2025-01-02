package com.trm.audiofeels.core.base.model

import co.touchlab.kermit.Logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

sealed interface LoadableState<out T> {
  data object Loading : LoadableState<Nothing>

  data class Success<T>(val value: T) : LoadableState<T>

  data class Error(val throwable: Throwable?) : LoadableState<Nothing>
}

fun <T> loadableStateFlowOf(
  timeout: Duration = DefaultTimeout,
  load: suspend () -> T,
): Flow<LoadableState<T>> =
  flow {
      emit(LoadableState.Loading)
      emit(LoadableState.Success(withTimeout(timeout) { load() }))
    }
    .catch {
      Logger.e(messageString = "LoadableStateError", throwable = it)
      emit(LoadableState.Error(it))
    }

internal val DefaultTimeout = 20.seconds
