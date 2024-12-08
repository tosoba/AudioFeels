package com.trm.audiofeels.core.base.util

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi

fun <T> CompletableDeferred<T>.onCompletion(action: T.() -> Unit) {
  invokeOnCompletion { @OptIn(ExperimentalCoroutinesApi::class) getCompleted().apply(action) }
}
