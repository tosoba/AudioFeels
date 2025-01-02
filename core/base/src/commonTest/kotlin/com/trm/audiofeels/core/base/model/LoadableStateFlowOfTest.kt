package com.trm.audiofeels.core.base.model

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class LoadableStateFlowOfTest {
  @Test
  fun `loadableStateFlowOf emits Loading then Success states when load succeeds`() = runTest {
    val expectedValue = "test-value"

    loadableStateFlowOf { expectedValue }
      .test {
        assertEquals(expected = LoadableState.Loading, actual = awaitItem())
        assertEquals(expected = LoadableState.Success(expectedValue), actual = awaitItem())
        awaitComplete()
        ensureAllEventsConsumed()
      }
  }

  @Test
  fun `loadableStateFlowOf emits Loading then Error states when load throws exception`() = runTest {
    val expectedException = RuntimeException("Test exception")

    loadableStateFlowOf { throw expectedException }
      .test {
        assertEquals(expected = LoadableState.Loading, actual = awaitItem())
        val error = awaitItem()
        assertTrue(error is LoadableState.Error)
        assertTrue(error.throwable is RuntimeException)
        assertEquals(expected = expectedException.message, actual = error.throwable!!.message)
        awaitComplete()
        ensureAllEventsConsumed()
      }
  }

  @Test
  fun `loadableStateFlowOf emits Loading then Error states when load times out`() = runTest {
    val timeout = 100.milliseconds

    loadableStateFlowOf(
        timeout = timeout,
        load = {
          delay(timeout * 2)
          "never-emitted"
        },
      )
      .test {
        assertEquals(expected = LoadableState.Loading, actual = awaitItem())
        val error = awaitItem()
        assertTrue(error is LoadableState.Error)
        assertTrue(error.throwable is TimeoutCancellationException)
        awaitComplete()
        ensureAllEventsConsumed()
      }
  }

  @Test
  fun `loadableStateFlowOf uses default timeout when not specified`() = runTest {
    val delayDuration = DefaultTimeout - 100.milliseconds

    loadableStateFlowOf {
        delay(delayDuration)
        "success"
      }
      .test {
        assertEquals(expected = LoadableState.Loading, actual = awaitItem())
        assertEquals(expected = LoadableState.Success("success"), actual = awaitItem())
        awaitComplete()
        ensureAllEventsConsumed()
      }
  }

  @Test
  fun `loadableStateFlowOf preserves custom timeout duration`() = runTest {
    val customTimeout = 5.seconds
    val delayDuration = 3.seconds
    val expectedValue = "success"

    loadableStateFlowOf(timeout = customTimeout) {
        delay(delayDuration)
        expectedValue
      }
      .test {
        assertEquals(expected = LoadableState.Loading, actual = awaitItem())
        assertEquals(expected = LoadableState.Success(expectedValue), actual = awaitItem())
        awaitComplete()
        ensureAllEventsConsumed()
      }
  }
}
