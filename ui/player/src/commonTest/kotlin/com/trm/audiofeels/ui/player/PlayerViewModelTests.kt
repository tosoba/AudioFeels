package com.trm.audiofeels.ui.player

import com.trm.audiofeels.core.test.PlayerFakeConnection
import com.trm.audiofeels.core.test.RobolectricTest
import com.trm.audiofeels.data.test.playbackInMemoryRepository
import com.trm.audiofeels.data.test.visualizationInMemoryRepository
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlayerViewModelTests : RobolectricTest() {
  @BeforeTest
  fun before() {
    Dispatchers.setMain(StandardTestDispatcher())
  }

  @AfterTest
  fun after() {
    Dispatchers.resetMain()
  }

  @Test fun test() = runTest { viewModel() }

  private fun viewModel(): PlayerViewModel {
    val hostsRepository = mock<HostsRepository> {}
    return PlayerViewModel(
      playerConnection = PlayerFakeConnection(),
      getPlayerInputUseCase = GetPlayerInputUseCase(mock {}, hostsRepository),
      playbackRepository = playbackInMemoryRepository(),
      visualizationRepository = visualizationInMemoryRepository(),
      hostsRepository = hostsRepository,
    )
  }
}
