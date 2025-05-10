package com.trm.audiofeels.ui.player

import app.cash.turbine.test
import com.trm.audiofeels.core.test.PlayerFakeConnection
import com.trm.audiofeels.core.test.RobolectricTest
import com.trm.audiofeels.core.test.stubPlaylist
import com.trm.audiofeels.core.test.stubTrack
import com.trm.audiofeels.data.test.playbackInMemoryRepository
import com.trm.audiofeels.data.test.visualizationInMemoryRepository
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
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

  @Test
  fun `given no current playlist - when no interaction - then currentPlaylist is null`() = runTest {
    viewModel().currentPlaylist.test {
      assertNull(awaitItem())
      expectNoEvents()
    }
  }

  @Test
  fun `given no current playlist - when start playlist playback - then playerViewState emits Loading and Playback`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"))
      val playerConnection = PlayerFakeConnection()
      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = mock { everySuspend { retrieveHost() } returns "audius-host.com" },
        )
        .playerViewState
        .test {
          awaitItem().startPlaylistPlayback(stubPlaylist(id = playlistId))
          assertIs<PlayerViewState.Loading>(awaitItem())
          val playback = awaitItem()
          assertIs<PlayerViewState.Playback>(playback)
          assertEquals(expected = playlistId, actual = playback.playlistId)
          assertIs<PlayerState.Enqueued>(playback.playerState)
          assertContentEquals(expected = tracks, actual = playback.tracks)
          assertEquals(expected = 0, actual = playback.currentTrackIndex)
          assertEquals(expected = 0.0, actual = playback.currentTrackProgress)
          expectNoEvents()
        }
    }

  private fun viewModel(
    playerConnection: PlayerConnection = PlayerFakeConnection(),
    playlistsRepository: PlaylistsRepository = mock {},
    hostsRepository: HostsRepository = mock {},
    recordAudioPermissionPermanentlyDenied: Boolean? = null,
  ): PlayerViewModel =
    PlayerViewModel(
      playerConnection = playerConnection,
      getPlayerInputUseCase = GetPlayerInputUseCase(playlistsRepository, hostsRepository),
      playbackRepository = playbackInMemoryRepository(),
      visualizationRepository =
        visualizationInMemoryRepository(recordAudioPermissionPermanentlyDenied),
      hostsRepository = hostsRepository,
    )
}
