package com.trm.audiofeels.ui.player

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.trm.audiofeels.core.test.PlayerFakeConnection
import com.trm.audiofeels.core.test.RobolectricTest
import com.trm.audiofeels.core.test.stubPlaylist
import com.trm.audiofeels.core.test.stubTrack
import com.trm.audiofeels.data.test.PlaybackInMemoryRepository
import com.trm.audiofeels.data.test.visualizationInMemoryRepository
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaybackRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import com.trm.audiofeels.ui.player.util.isPlaying
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
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
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
internal class PlayerViewModelTests : RobolectricTest() {
  private lateinit var playbackInMemoryRepository: PlaybackInMemoryRepository

  @BeforeTest
  fun before() {
    Dispatchers.setMain(StandardTestDispatcher())
    playbackInMemoryRepository = PlaybackInMemoryRepository()
  }

  @AfterTest
  fun after() {
    playbackInMemoryRepository.close()
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
  fun `given initial current playlist - when toggle favourite - then currentPlaylist is favourite state changes`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"))
      setCurrentPlaylist(playlistId)
      val playerConnection = PlayerFakeConnection()

      val viewModel =
        viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = stubDefaultHostsRepository(),
        )

      viewModel.currentPlaylist.test {
        skipItems(1)

        assertEquals(expected = false, actual = awaitItem()?.favourite)

        viewModel.playerViewState
          .transformWhile {
            emit(it)
            it !is PlayerViewState.Playback
          }
          .test {
            awaitUntilViewStateIsInstance<PlayerViewState.Playback>().togglePlaylistFavourite()
            playerConnection.reset()
            awaitComplete()
          }

        assertEquals(expected = true, actual = awaitItem()?.favourite)

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
          hostsRepository = stubDefaultHostsRepository(),
        )
        .playerViewState
        .transformWhile {
          emit(it)
          it !is PlayerViewState.Playback
        }
        .test {
          awaitItem().startPlaylistPlayback(stubPlaylist(id = playlistId))

          assertIs<PlayerViewState.Loading>(awaitItem())

          assertPlaybackPlayerEnqueuedViewState(
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
  fun `given no current playlist - when start playlist playback and pause - then playerViewState emits Playback playing and Playback paused after loading completed`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"))
      val playerConnection = PlayerFakeConnection()

      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = stubDefaultHostsRepository(),
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
          assertIs<PlayerViewState.Playback>(initialPlaybackViewState)
          val primaryControlState = initialPlaybackViewState.primaryControlState
          assertIs<PlayerPrimaryControlState.Action>(primaryControlState)
          primaryControlState.action()

          assertPlaybackPlayerEnqueuedViewState(
            viewState = awaitPlaybackPausedViewState(),
            expectedPlaylistId = playlistId,
            expectedIsPlaying = false,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 0,
          )

          playerConnection.reset()

          awaitComplete()
        }
    }

  @Test
  fun `given no current playlist - when start the same playlist playback twice - then playerViewState emits Playback playing and Playback paused after loading completed`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"))
      val playerConnection = PlayerFakeConnection()

      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = stubDefaultHostsRepository(),
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
          assertIs<PlayerViewState.Playback>(initialPlaybackViewState)
          initialPlaybackViewState.startPlaylistPlayback(stubPlaylist(id = playlistId))

          assertPlaybackPlayerEnqueuedViewState(
            viewState = awaitPlaybackPausedViewState(),
            expectedPlaylistId = playlistId,
            expectedIsPlaying = false,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 0,
          )

          playerConnection.reset()

          awaitComplete()
        }
    }

  @Test
  fun `given no current playlist - when start playlist playback and cancel playback - then playerViewState emits Invisible after cancel playback`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"))
      val playerConnection = PlayerFakeConnection()
      val viewModel =
        viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = stubDefaultHostsRepository(),
        )

      viewModel.playerViewState
        .withIndex()
        .transformWhile { (index, viewState) ->
          emit(viewState)
          index == 0 || viewState !is PlayerViewState.Invisible
        }
        .test {
          awaitItem().startPlaylistPlayback(stubPlaylist(id = playlistId))

          assertIs<PlayerViewState.Loading>(awaitItem())

          val initialPlaybackViewState = awaitItem()
          assertIs<PlayerViewState.Playback>(initialPlaybackViewState)
          initialPlaybackViewState.cancelPlayback()

          assertIs<PlayerViewState.Invisible>(
            awaitViewStateWhile { it is PlayerViewState.Playback }
          )

          awaitComplete()
        }
    }

  @Test
  fun `given no current playlist and error response from playlistTracks - when start playlist - then playerViewState emits Loading and Error`() =
    runTest {
      val playlistId = "playlist-1"

      viewModel(
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } throws Throwable() },
          hostsRepository = stubDefaultHostsRepository(),
        )
        .playerViewState
        .transformWhile {
          emit(it)
          it !is PlayerViewState.Error
        }
        .test {
          awaitItem().startPlaylistPlayback(stubPlaylist(id = playlistId))
          assertIs<PlayerViewState.Loading>(awaitItem())
          assertIs<PlayerViewState.Error>(awaitItem())
          awaitComplete()
        }
    }

  @Test
  fun `given no current playlist and error response from retrieveHost - when start playlist - then playerViewState emits Loading and Error`() =
    runTest {
      val playlistId = "playlist-1"

      viewModel(
          playlistsRepository =
            mock {
              everySuspend { getPlaylistTracks(eq(playlistId)) } returns
                listOf(stubTrack(id = "track-1"))
            },
          hostsRepository = mock { everySuspend { retrieveHost() } throws Throwable() },
        )
        .playerViewState
        .transformWhile {
          emit(it)
          it !is PlayerViewState.Error
        }
        .test {
          awaitItem().startPlaylistPlayback(stubPlaylist(id = playlistId))
          assertIs<PlayerViewState.Loading>(awaitItem())
          assertIs<PlayerViewState.Error>(awaitItem())
          awaitComplete()
        }
    }

  @Test
  fun `given no current playlist and initial error from playlistTracks - when retry - then playerViewState emits Loading and Playback after successful retry`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"))
      val playerConnection = PlayerFakeConnection()

      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock {
              everySuspend { getPlaylistTracks(eq(playlistId)) } sequentially
                {
                  throws(Throwable())
                  returns(tracks)
                }
            },
          hostsRepository = stubDefaultHostsRepository(),
        )
        .playerViewState
        .transformWhile {
          emit(it)
          it !is PlayerViewState.Playback
        }
        .test {
          awaitItem().startPlaylistPlayback(stubPlaylist(id = playlistId))
          assertIs<PlayerViewState.Loading>(awaitItem())

          val errorViewState = awaitItem()
          assertIs<PlayerViewState.Error>(errorViewState)
          errorViewState.primaryControlState.action()

          assertIs<PlayerViewState.Invisible>(awaitItem())
          assertIs<PlayerViewState.Loading>(awaitItem())

          assertPlaybackPlayerEnqueuedViewState(
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
  fun `given initial current playlist - when no interaction - then playerViewState emits paused Playback after loading completed`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"))
      val playerConnection = PlayerFakeConnection()
      setCurrentPlaylist(playlistId)

      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = stubDefaultHostsRepository(),
        )
        .playerViewState
        .transformWhile {
          emit(it)
          it !is PlayerViewState.Playback
        }
        .test {
          skipItems(2)

          assertPlaybackPlayerIdleViewState(
            viewState = awaitItem(),
            expectedPlaylistId = playlistId,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 0,
            expectedCurrentTrackProgress = 0.0,
          )

          playerConnection.reset()

          awaitComplete()
        }
    }

  @Test
  fun `given initial current playlist - when play next - then playerViewState emits Playback with next track`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"), stubTrack(id = "track-2"))
      val playerConnection = PlayerFakeConnection()
      setCurrentPlaylist(playlistId)

      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = stubDefaultHostsRepository(),
        )
        .playerViewState
        .transformWhile {
          emit(it)
          !it.isPlaying
        }
        .test {
          skipItems(2)

          val initialPlaybackViewState = awaitItem()
          assertPlaybackPlayerIdleViewState(
            viewState = initialPlaybackViewState,
            expectedPlaylistId = playlistId,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 0,
            expectedCurrentTrackProgress = 0.0,
          )

          initialPlaybackViewState.playNextTrack()

          assertPlaybackPlayerEnqueuedViewState(
            viewState = awaitItem(),
            expectedPlaylistId = playlistId,
            expectedIsPlaying = true,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 1,
            expectedCurrentTrackProgress = 0.0,
          )

          playerConnection.reset()

          awaitComplete()
        }
    }

  @Test
  fun `given initial current playlist - when play previous - then playerViewState emits Playback with previous track`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks = listOf(stubTrack(id = "track-1"), stubTrack(id = "track-2"))
      val playerConnection = PlayerFakeConnection()
      setCurrentPlaylist(playlistId = playlistId, currentTrackIndex = 1)

      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = stubDefaultHostsRepository(),
        )
        .playerViewState
        .transformWhile {
          emit(it)
          !it.isPlaying
        }
        .test {
          skipItems(2)

          val initialPlaybackViewState = awaitItem()
          assertPlaybackPlayerIdleViewState(
            viewState = initialPlaybackViewState,
            expectedPlaylistId = playlistId,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 1,
            expectedCurrentTrackProgress = 0.0,
          )

          initialPlaybackViewState.playPreviousTrack()

          assertPlaybackPlayerEnqueuedViewState(
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
  fun `given initial current playlist - when play at index - then playerViewState emits Playback with track at index`() =
    runTest {
      val playlistId = "playlist-1"
      val tracks =
        listOf(stubTrack(id = "track-1"), stubTrack(id = "track-2"), stubTrack(id = "track-3"))
      val expectedTrackIndex = 2
      val playerConnection = PlayerFakeConnection()
      setCurrentPlaylist(playlistId = playlistId, currentTrackIndex = 0)

      viewModel(
          playerConnection = playerConnection,
          playlistsRepository =
            mock { everySuspend { getPlaylistTracks(eq(playlistId)) } returns tracks },
          hostsRepository = stubDefaultHostsRepository(),
        )
        .playerViewState
        .transformWhile {
          emit(it)
          !it.isPlaying
        }
        .test {
          skipItems(2)

          val initialPlaybackViewState = awaitItem()
          assertPlaybackPlayerIdleViewState(
            viewState = initialPlaybackViewState,
            expectedPlaylistId = playlistId,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = 0,
            expectedCurrentTrackProgress = 0.0,
          )

          initialPlaybackViewState.playTrackAtIndex(expectedTrackIndex)

          assertPlaybackPlayerEnqueuedViewState(
            viewState = awaitItem(),
            expectedPlaylistId = playlistId,
            expectedIsPlaying = true,
            expectedTracks = tracks,
            expectedCurrentTrackIndex = expectedTrackIndex,
            expectedCurrentTrackProgress = 0.0,
          )

          playerConnection.reset()

          awaitComplete()
        }
    }

  private suspend fun setCurrentPlaylist(
    playlistId: String,
    currentTrackIndex: Int = 0,
    currentTrackPositionMs: Long = 0L,
  ) {
    playbackInMemoryRepository.setNewCurrentPlaylist(
      playlist = stubPlaylist(id = playlistId),
      carryOn = false,
    )
    playbackInMemoryRepository.updateCurrentPlaylist(
      id = playlistId,
      currentTrackIndex = currentTrackIndex,
      currentTrackPositionMs = currentTrackPositionMs,
    )
  }

  private fun stubDefaultHostsRepository(): HostsRepository = mock {
    everySuspend { retrieveHost() } returns DEFAULT_HOST
  }

  private suspend fun TurbineTestContext<PlayerViewState>.awaitPlaybackPausedViewState() =
    awaitViewStateWhile(PlayerViewState::isPlaying)

  private suspend fun TurbineTestContext<PlayerViewState>.awaitViewStateWhile(
    condition: (PlayerViewState) -> Boolean
  ): PlayerViewState {
    var viewState = awaitItem()
    while (condition(viewState)) viewState = awaitItem()
    return viewState
  }

  private suspend inline fun <reified T : PlayerViewState> TurbineTestContext<PlayerViewState>
    .awaitUntilViewStateIsInstance(): T {
    var viewState = awaitItem()
    while (viewState !is T) viewState = awaitItem()
    return viewState
  }

  @OptIn(ExperimentalContracts::class)
  private fun assertPlaybackPlayerEnqueuedViewState(
    viewState: PlayerViewState,
    expectedPlaylistId: String,
    expectedIsPlaying: Boolean,
    expectedTracks: List<Track>,
    expectedCurrentTrackIndex: Int,
    expectedCurrentTrackProgress: Double? = null,
  ) {
    contract { returns() implies (viewState is PlayerViewState.Playback) }

    assertIs<PlayerViewState.Playback>(viewState)
    assertPlaybackViewState(
      viewState = viewState,
      expectedPlaylistId = expectedPlaylistId,
      expectedTracks = expectedTracks,
      expectedCurrentTrackIndex = expectedCurrentTrackIndex,
      expectedCurrentTrackProgress = expectedCurrentTrackProgress,
    )

    val playerState = viewState.playerState
    assertIs<PlayerState.Enqueued>(playerState)
    assertEquals(expected = expectedIsPlaying, actual = playerState.isPlaying)
    assertEquals(
      expected = expectedTracks[expectedCurrentTrackIndex],
      actual = playerState.currentTrack,
    )
  }

  @OptIn(ExperimentalContracts::class)
  private fun assertPlaybackPlayerIdleViewState(
    viewState: PlayerViewState,
    expectedPlaylistId: String,
    expectedTracks: List<Track>,
    expectedCurrentTrackIndex: Int,
    expectedCurrentTrackProgress: Double? = null,
  ) {
    contract { returns() implies (viewState is PlayerViewState.Playback) }

    assertIs<PlayerViewState.Playback>(viewState)
    assertPlaybackViewState(
      viewState = viewState,
      expectedPlaylistId = expectedPlaylistId,
      expectedTracks = expectedTracks,
      expectedCurrentTrackIndex = expectedCurrentTrackIndex,
      expectedCurrentTrackProgress = expectedCurrentTrackProgress,
    )

    assertIs<PlayerState.Idle>(viewState.playerState)
  }

  private fun assertPlaybackViewState(
    viewState: PlayerViewState.Playback,
    expectedPlaylistId: String,
    expectedTracks: List<Track>,
    expectedCurrentTrackIndex: Int,
    expectedCurrentTrackProgress: Double?,
  ) {
    assertEquals(expected = expectedPlaylistId, actual = viewState.playlistId)
    assertContentEquals(expected = expectedTracks, actual = viewState.tracks)
    assertEquals(expected = expectedCurrentTrackIndex, actual = viewState.currentTrackIndex)
    expectedCurrentTrackProgress?.let {
      assertEquals(expected = it, actual = viewState.currentTrackProgress)
    }
  }

  private fun viewModel(
    playerConnection: PlayerConnection = PlayerFakeConnection(),
    playbackRepository: PlaybackRepository = playbackInMemoryRepository,
    playlistsRepository: PlaylistsRepository = mock {},
    hostsRepository: HostsRepository = mock {},
    recordAudioPermissionPermanentlyDenied: Boolean? = null,
  ): PlayerViewModel =
    PlayerViewModel(
      playerConnection = playerConnection,
      getPlayerInputUseCase = GetPlayerInputUseCase(playlistsRepository, hostsRepository),
      playbackRepository = playbackRepository,
      visualizationRepository =
        visualizationInMemoryRepository(recordAudioPermissionPermanentlyDenied),
      hostsRepository = hostsRepository,
    )

  companion object {
    private const val DEFAULT_HOST = "audius-host.com"
  }
}
