package com.trm.audiofeels.ui.player

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlayerViewModelTests {
  @BeforeTest
  fun before() {
    Dispatchers.setMain(StandardTestDispatcher())
  }

  @AfterTest
  fun after() {
    Dispatchers.resetMain()
  }
}
