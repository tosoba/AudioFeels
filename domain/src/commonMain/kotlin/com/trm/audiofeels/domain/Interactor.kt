package com.trm.audiofeels.domain

import com.trm.audiofeels.core.base.util.cancellableRunCatching
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeout

abstract class Interactor<in P, R> {
  private val loadingState = MutableStateFlow(LoadingState())

  val inProgress: Flow<Boolean> by lazy { loadingState.map { it.count > 0 }.distinctUntilChanged() }

  private fun addLoader() {
    loadingState.update { it.copy(count = it.count + 1) }
  }

  private fun removeLoader() {
    loadingState.update { it.copy(count = it.count - 1) }
  }

  suspend operator fun invoke(params: P, timeout: Duration = DefaultTimeout): Result<R> =
    cancellableRunCatching {
        addLoader()
        withTimeout(timeout) { doWork(params) }
      }
      .also { removeLoader() }

  protected abstract suspend fun doWork(params: P): R

  private data class LoadingState(val count: Int = 0)

  companion object {
    internal val DefaultTimeout = 30.seconds
  }
}

suspend operator fun <R> Interactor<Unit, R>.invoke(timeout: Duration = Interactor.DefaultTimeout) =
  invoke(Unit, timeout)

@OptIn(ExperimentalCoroutinesApi::class)
abstract class FlowInteractor<P : Any, T> {
  private val paramState =
    MutableSharedFlow<P>(
      replay = 1,
      extraBufferCapacity = 1,
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

  val flow: Flow<T> =
    paramState.distinctUntilChanged().flatMapLatest(::doWork).distinctUntilChanged()

  operator fun invoke(params: P) {
    paramState.tryEmit(params)
  }

  protected abstract fun doWork(params: P): Flow<T>
}
