package com.trm.audiofeels.base.util

import kotlin.coroutines.cancellation.CancellationException

inline fun <T, R> T.cancellableRunCatching(block: T.() -> R): Result<R> = try {
  Result.success(block())
} catch (ce: CancellationException) {
  throw ce
} catch (e: Throwable) {
  Result.failure(e)
}
