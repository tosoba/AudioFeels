package com.trm.audiofeels.ui.player

import app.cash.turbine.test
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
import kotlin.test.assertEquals
import kotlin.test.assertIs
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

  @Test
  fun `requestRecordAudioPermission emits false by default`() = runTest {
    viewModel().requestRecordAudioPermission.test {
      assertEquals(expected = false, actual = awaitItem())
      expectNoEvents()
    }
  }

  @Test
  fun `given permission not permanently denied - then requestRecordAudioPermission emits true`() =
    runTest {
      viewModel(recordAudioPermissionPermanentlyDenied = false).requestRecordAudioPermission.test {
        skipItems(1)
        assertEquals(expected = true, actual = awaitItem())
        expectNoEvents()
      }
    }

  @Test
  fun `given permission permanently denied - then requestRecordAudioPermission emits false`() =
    runTest {
      viewModel(recordAudioPermissionPermanentlyDenied = true).requestRecordAudioPermission.test {
        assertEquals(expected = false, actual = awaitItem())
        expectNoEvents()
      }
    }

  @Test
  fun `given no current playlist - when no interaction - then playerViewState is Invisible`() =
    runTest {
      viewModel().playerViewState.test {
        assertIs<PlayerViewState.Invisible>(awaitItem())
        expectNoEvents()
      }
    }

  private fun viewModel(recordAudioPermissionPermanentlyDenied: Boolean? = null): PlayerViewModel {
    val hostsRepository = mock<HostsRepository> {}
    return PlayerViewModel(
      playerConnection = PlayerFakeConnection(),
      getPlayerInputUseCase = GetPlayerInputUseCase(mock {}, hostsRepository),
      playbackRepository = playbackInMemoryRepository(),
      visualizationRepository =
        visualizationInMemoryRepository(recordAudioPermissionPermanentlyDenied),
      hostsRepository = hostsRepository,
    )
  }
}
