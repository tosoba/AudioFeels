package com.trm.audiofeels.ui.player

import app.cash.turbine.test
import com.trm.audiofeels.core.test.PlayerFakeConnection
import com.trm.audiofeels.core.test.RobolectricTest
import com.trm.audiofeels.core.test.stubPlaylist
import com.trm.audiofeels.core.test.stubTrack
import com.trm.audiofeels.data.test.playbackInMemoryRepository
import com.trm.audiofeels.data.test.visualizationInMemoryRepository
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import com.trm.audiofeels.ui.player.util.isPlaying
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
import kotlinx.coroutines.flow.transformWhile
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
          hostsRepository = mock { everySuspend { retrieveHost() } returns DEFAULT_HOST },
        )
        .playerViewState
        .transformWhile {
          emit(it)
          it !is PlayerViewState.Playback
        }
        .test {
          awaitItem().startPlaylistPlayback(stubPlaylist(id = playlistId))

          assertIs<PlayerViewState.Loading>(awaitItem())

          assertPlaybackViewState(
            viewState = awaitItem(),
            expectedPlaylistId = playlistId,
            expectedIsPlaying = true,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 0,
            expectedCurrentTrackProgress = 0.0,
          )

          playerConnection.reset()

          awaitComplete()
        }
    }

  @Test
  fun `given no current playlist - when start playlist playback and pause - then playerViewState emits Loading and Playback playing and Playback paused`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"))
      val playerConnection = PlayerFakeConnection()

      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = mock { everySuspend { retrieveHost() } returns DEFAULT_HOST },
        )
        .playerViewState
        .transformWhile {
          emit(it)
          it !is PlayerViewState.Playback || it.isPlaying
        }
        .test {
          awaitItem().startPlaylistPlayback(stubPlaylist(id = playlistId))

          assertIs<PlayerViewState.Loading>(awaitItem())

          val initialPlaybackViewState = awaitItem()
          assertPlaybackViewState(
            viewState = initialPlaybackViewState,
            expectedPlaylistId = playlistId,
            expectedIsPlaying = true,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 0,
            expectedCurrentTrackProgress = 0.0,
          )

          val primaryControlState = initialPlaybackViewState.primaryControlState
          assertIs<PlayerPrimaryControlState.Action>(primaryControlState)
          primaryControlState.action()

          var nextPlaybackViewState = awaitItem()
          while (nextPlaybackViewState.isPlaying) nextPlaybackViewState = awaitItem()
          assertPlaybackViewState(
            viewState = nextPlaybackViewState,
            expectedPlaylistId = playlistId,
            expectedIsPlaying = false,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 0,
          )

          playerConnection.reset()

          awaitComplete()
        }
    }

  private fun assertPlaybackViewState(
    viewState: PlayerViewState,
    expectedPlaylistId: String,
    expectedIsPlaying: Boolean,
    expectedTracks: List<Track>,
    expectedCurrentTrackIndex: Int,
    expectedCurrentTrackProgress: Double? = null,
  ) {
    assertIs<PlayerViewState.Playback>(viewState)
    assertEquals(expected = expectedPlaylistId, actual = viewState.playlistId)

    val playerState = viewState.playerState
    assertIs<PlayerState.Enqueued>(playerState)
    assertEquals(expected = expectedIsPlaying, actual = playerState.isPlaying)
    assertEquals(
      expected = expectedTracks[expectedCurrentTrackIndex],
      actual = playerState.currentTrack,
    )

    assertContentEquals(expected = expectedTracks, actual = viewState.tracks)
    assertEquals(expected = expectedCurrentTrackIndex, actual = viewState.currentTrackIndex)
    expectedCurrentTrackProgress?.let {
      assertEquals(expected = it, actual = viewState.currentTrackProgress)
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

  companion object {
    private const val DEFAULT_HOST = "audius-host.com"
  }
}
