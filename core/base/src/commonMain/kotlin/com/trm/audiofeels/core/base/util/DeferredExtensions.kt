package com.trm.audiofeels.core.base.util

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi

fun <T> Deferred<T>.onCompletion(action: T.() -> Unit) {
  invokeOnCompletion { @OptIn(ExperimentalCoroutinesApi::class) getCompleted().apply(action) }
}
