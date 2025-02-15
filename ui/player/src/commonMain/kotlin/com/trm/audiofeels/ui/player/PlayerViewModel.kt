package com.trm.audiofeels.ui.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.PlatformContext
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.core.base.util.roundTo
import com.trm.audiofeels.core.ui.compose.util.loadImageBitmapOrNull
import com.trm.audiofeels.domain.model.PlaybackState
import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playerConnection: PlayerConnection,
  private val getPlayerInputUseCase: GetPlayerInputUseCase,
  private val playlistsRepository: PlaylistsRepository,
  private val imageLoader: ImageLoader,
  private val platformContext: PlatformContext,
) : ViewModel() {
  val viewState: RestartableStateFlow<PlayerViewState> =
    playlistsRepository
      .getCurrentPlaylistPlaybackFlow()
      .distinctUntilChangedBy { playback -> playback?.playlist?.id }
      .flatMapLatest { playback ->
        playback?.let(::playerViewStateFlow)
          ?: flowOf(PlayerViewState.Invisible(playerViewPlaybackActions())).onEach {
            playerConnection.reset()
          }
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = PlayerViewState.Invisible(playerViewPlaybackActions()),
      )

  private fun playerViewStateFlow(playback: PlaylistPlayback): Flow<PlayerViewState> =
    loadableStateFlowOf { getPlayerInputUseCase(playback.playlist.id) }
      .onEach { input ->
        if (input is LoadableState.Idle && playback.autoPlay) {
          playerConnection.enqueue(
            input = input.value,
            startTrackIndex = playback.currentTrackIndex,
            startPositionMs = playback.currentTrackPositionMs,
          )
        }
      }
      .flatMapLatest { playerInput -> playerViewStateFlow(playerInput, playback) }
      .onEach { if (it is PlayerViewState.Playback) onPlayerViewStatePlayback(it) }

  private fun playerViewStateFlow(
    input: LoadableState<PlayerInput>,
    playback: PlaylistPlayback,
  ): Flow<PlayerViewState> =
    when (input) {
      LoadableState.Loading -> {
        flowOf(
          PlayerViewState.Loading(
            playlist = playback.playlist,
            playbackActions = playerViewPlaybackActions(),
          )
        )
      }
      is LoadableState.Idle -> {
        playbackViewStateFlow(input.value, playback)
      }
      is LoadableState.Error -> {
        flowOf(
          PlayerViewState.Error(
            playlist = playback.playlist,
            playbackActions = playerViewPlaybackActions(),
            primaryControlState = restartViewStateAction(),
          )
        )
      }
    }

  private fun playbackViewStateFlow(
    input: PlayerInput,
    playback: PlaylistPlayback,
  ): Flow<PlayerViewState> {
    val artworkUrlChannel = Channel<String?>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    return combine(
      playerConnection.playerState.onEach {
        artworkUrlChannel.send(getCurrentTrackArtworkUrl(it, input, playback))
      },
      artworkUrlChannel.receiveAsFlow().distinctUntilChanged().transformLatest { artworkUrl ->
        artworkUrl?.let {
          emit(LoadableState.Loading)
          emit(
            imageLoader.loadImageBitmapOrNull(it, platformContext)?.let { artwork ->
              LoadableState.Idle(artwork)
            } ?: LoadableState.Error(Exception("Error loading artwork"))
          )
        } ?: run { emit(LoadableState.Error(Exception("Missing artworkUrl"))) }
      },
      playerConnection.currentTrackPositionMs.distinctUntilChanged().onStart { emit(0L) },
    ) { playerState, currentTrackImageBitmap, currentTrackPositionMs ->
      playbackViewState(
        playerInput = input,
        playback = playback,
        playerState = playerState,
        currentTrackPositionMs =
          if (playerState is PlayerState.Idle) playback.currentTrackPositionMs
          else currentTrackPositionMs,
        currentTrackImageBitmap = currentTrackImageBitmap,
      )
    }
  }

  private fun playbackViewState(
    playerInput: PlayerInput,
    playback: PlaylistPlayback,
    playerState: PlayerState,
    currentTrackPositionMs: Long,
    currentTrackImageBitmap: LoadableState<ImageBitmap?>,
  ): PlayerViewState.Playback {
    val controlActions = playerViewControlActions(playerState, playerInput, playback)
    val togglePlay = { togglePlay(playerState, playerInput, playback) }
    return PlayerViewState.Playback(
      playlist = playback.playlist,
      playerState = playerState,
      tracks = playerInput.tracks,
      currentTrackIndex = getCurrentTrackIndex(playerState, playback),
      currentTrackProgress = currentTrackProgress(playerState, currentTrackPositionMs),
      currentTrackImageBitmap = currentTrackImageBitmap,
      primaryControlState = primaryControlState(playerState, togglePlay),
      playbackActions =
        playerViewPlaybackActions(currentPlaylist = playback.playlist, togglePlay = togglePlay),
      controlActions = controlActions,
    )
  }

  private fun primaryControlState(
    playerState: PlayerState,
    togglePlay: () -> Unit,
  ): PlayerViewState.PrimaryControlState =
    when (playerState) {
      PlayerState.Idle -> {
        playAction(togglePlay)
      }
      is PlayerState.Enqueued -> {
        when {
          playerState.playbackState == PlaybackState.BUFFERING -> {
            PlayerViewState.PrimaryControlState.Loading
          }
          playerState.isPlaying -> {
            pauseAction(togglePlay)
          }
          else -> {
            playAction(togglePlay)
          }
        }
      }
      is PlayerState.Error -> {
        restartViewStateAction()
      }
    }

  private fun pauseAction(togglePlay: () -> Unit) =
    PlayerViewState.PrimaryControlState.Action(
      imageVector = Icons.Outlined.Pause,
      contentDescription = "Pause",
      action = togglePlay,
    )

  private fun playAction(togglePlay: () -> Unit) =
    PlayerViewState.PrimaryControlState.Action(
      imageVector = Icons.Outlined.PlayArrow,
      contentDescription = "Play",
      action = togglePlay,
    )

  private fun restartViewStateAction(): PlayerViewState.PrimaryControlState.Action =
    PlayerViewState.PrimaryControlState.Action(
      imageVector = Icons.Outlined.Refresh,
      contentDescription = "Retry",
      action = viewState::restart,
    )

  private fun currentTrackProgress(playerState: PlayerState, currentTrackPositionMs: Long): Double =
    when (playerState) {
      is PlayerState.Enqueued -> {
        currentTrackPositionMs.toDouble() / playerState.currentTrack.duration.toDouble() / 1000.0
      }
      PlayerState.Idle,
      is PlayerState.Error -> {
        0.0
      }
    }.roundTo(3)

  private fun playerViewPlaybackActions(
    currentPlaylist: Playlist,
    togglePlay: () -> Unit,
  ): PlayerViewPlaybackActions =
    object : PlayerViewPlaybackActions {
      override fun start(playlist: Playlist) {
        if (playlist != currentPlaylist) startNewPlaylistPlayback(playlist) else togglePlay()
      }

      override fun cancel() {
        cancelPlayback()
      }
    }

  private fun playerViewPlaybackActions(): PlayerViewPlaybackActions =
    object : PlayerViewPlaybackActions {
      override fun start(playlist: Playlist) {
        startNewPlaylistPlayback(playlist)
      }

      override fun cancel() {
        cancelPlayback()
      }
    }

  private fun startNewPlaylistPlayback(playlist: Playlist) {
    viewModelScope.launch { playlistsRepository.setNewCurrentPlaylist(playlist) }
  }

  private fun cancelPlayback() {
    viewModelScope.launch { playlistsRepository.clearCurrentPlaylist() }
  }

  private fun togglePlay(
    playerState: PlayerState,
    playerInput: PlayerInput,
    playback: PlaylistPlayback,
  ) {
    when (playerState) {
      PlayerState.Idle -> {
        playerConnection.enqueue(
          input = playerInput,
          startTrackIndex = playback.currentTrackIndex,
          startPositionMs = playback.currentTrackPositionMs,
        )
      }
      is PlayerState.Enqueued -> {
        if (playerState.isPlaying) playerConnection.pause() else playerConnection.play()
      }
      is PlayerState.Error -> {
        return
      }
    }
  }

  private fun playerViewControlActions(
    playerState: PlayerState,
    playerInput: PlayerInput,
    playback: PlaylistPlayback,
  ): PlayerViewControlActions =
    object : PlayerViewControlActions {
      override fun playPrevious() {
        when (playerState) {
          PlayerState.Idle -> {
            playerConnection.enqueue(
              input = playerInput,
              startTrackIndex = (playback.currentTrackIndex - 1).coerceAtLeast(0),
              startPositionMs = 0L,
            )
          }
          is PlayerState.Enqueued -> {
            playerConnection.playPrevious()
          }
          is PlayerState.Error -> {
            return
          }
        }
      }

      override fun playNext() {
        when (playerState) {
          PlayerState.Idle -> {
            playerConnection.enqueue(
              input = playerInput,
              startTrackIndex =
                (playback.currentTrackIndex + 1).coerceAtMost(playerInput.tracks.lastIndex),
              startPositionMs = 0L,
            )
          }
          is PlayerState.Enqueued -> {
            playerConnection.playNext()
          }
          is PlayerState.Error -> {
            return
          }
        }
      }
    }

  private fun onPlayerViewStatePlayback(playbackState: PlayerViewState.Playback) {
    Napier.d(tag = "PLAYER_STATE", message = playbackState.playerState.toString())
    when (val playerState = playbackState.playerState) {
      PlayerState.Idle -> {
        return
      }
      is PlayerState.Enqueued -> {
        viewModelScope.launch {
          playlistsRepository.updateCurrentPlaylist(
            PlaylistPlayback(
              playlist = playbackState.playlist,
              currentTrackIndex = playerState.currentTrackIndex,
              currentTrackPositionMs =
                playerState.currentTrack.positionMsOf(playbackState.currentTrackProgress),
              autoPlay = false,
            )
          )
        }
      }
      is PlayerState.Error -> {
        // TODO: error handling
      }
    }
  }

  private fun getCurrentTrackArtworkUrl(
    playerState: PlayerState,
    input: PlayerInput,
    playback: PlaylistPlayback,
  ): String? {
    fun initialTrackArtworkUrl(input: PlayerInput, playback: PlaylistPlayback): String? =
      input.tracks.getOrNull(playback.currentTrackIndex)?.artworkUrl

    return when (playerState) {
      PlayerState.Idle -> {
        initialTrackArtworkUrl(input, playback)
      }
      is PlayerState.Enqueued -> {
        playerState.currentTrack.artworkUrl
      }
      is PlayerState.Error -> {
        playerState.previousEnqueuedState?.currentTrack?.artworkUrl
          ?: initialTrackArtworkUrl(input, playback)
      }
    }
  }

  private fun getCurrentTrackIndex(playerState: PlayerState, playback: PlaylistPlayback): Int =
    when (playerState) {
      PlayerState.Idle -> {
        playback.currentTrackIndex
      }
      is PlayerState.Enqueued -> {
        playerState.currentTrackIndex
      }
      is PlayerState.Error -> {
        playerState.previousEnqueuedState?.currentTrackIndex ?: playback.currentTrackIndex
      }
    }
}
