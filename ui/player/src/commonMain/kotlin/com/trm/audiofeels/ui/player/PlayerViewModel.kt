package com.trm.audiofeels.ui.player

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
import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.PlaybackRepository
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
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playerConnection: PlayerConnection,
  private val getPlayerInputUseCase: GetPlayerInputUseCase,
  private val playbackRepository: PlaybackRepository,
  private val playlistsRepository: PlaylistsRepository,
  private val imageLoader: ImageLoader,
  private val platformContext: PlatformContext,
) : ViewModel() {
  val viewState: RestartableStateFlow<PlayerViewState> =
    playbackRepository
      .getPlaybackPlaylistFlow()
      .distinctUntilChangedBy { playlist -> playlist?.id }
      .scan(Pair<Playlist?, Playlist?>(null, null)) { (_, previous), current ->
        previous to current
      }
      .onEach { (previous) ->
        previous?.let { viewModelScope.launch { playlistsRepository.savePlaylist(it) } }
      }
      .flatMapLatest { (_, playlist) ->
        playlist?.let(::playerViewStateFlow)
          ?: flowOf(PlayerViewState.Invisible(playerViewPlaybackActions())).onEach {
            playerConnection.reset()
          }
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = PlayerViewState.Invisible(playerViewPlaybackActions()),
      )

  private fun playerViewStateFlow(playlist: Playlist): Flow<PlayerViewState> =
    loadableStateFlowOf { getPlayerInputUseCase(playlist.id) }
      .onEach { input ->
        if (input is LoadableState.Success && input.value.start.autoPlay) {
          enqueue(input.value)
        }
      }
      .flatMapLatest { playerInput -> playerInput.toPlayerViewStateFlow(playlist) }
      .onEach { if (it is PlayerViewState.Playback) onPlayerViewStatePlayback(it) }

  private fun LoadableState<PlayerInput>.toPlayerViewStateFlow(
    playlist: Playlist
  ): Flow<PlayerViewState> =
    when (this) {
      LoadableState.Loading -> {
        flowOf(
          PlayerViewState.Loading(
            playlist = playlist,
            playbackActions = playerViewPlaybackActions(),
          )
        )
      }
      is LoadableState.Success -> {
        val artworkUrlChannel = Channel<String?>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
        combine(
          playerConnection.playerState.onEach {
            artworkUrlChannel.send(getCurrentTrackArtworkUrl(it, value))
          },
          artworkUrlChannel.receiveAsFlow().distinctUntilChanged().transformLatest { artworkUrl ->
            emit(null)
            artworkUrl?.let { emit(imageLoader.loadImageBitmapOrNull(it, platformContext)) }
          },
          playerConnection.currentTrackPositionMs.distinctUntilChanged().onStart { emit(0L) },
        ) { playerState, currentTrackImageBitmap, currentTrackPositionMs ->
          playbackViewState(
            playerInput = value,
            playlist = playlist,
            playerState = playerState,
            currentTrackPositionMs =
              if (playerState is PlayerState.Idle) value.start.trackPositionMs
              else currentTrackPositionMs,
            currentTrackImageBitmap = currentTrackImageBitmap,
          )
        }
      }
      is LoadableState.Error -> {
        flowOf(
          PlayerViewState.Error(playlist = playlist, playbackActions = playerViewPlaybackActions())
        )
      }
    }

  private fun playbackViewState(
    playerInput: PlayerInput,
    playlist: Playlist,
    playerState: PlayerState,
    currentTrackPositionMs: Long,
    currentTrackImageBitmap: ImageBitmap?,
  ): PlayerViewState.Playback {
    val controlActions = playerViewControlActions(playerState, playerInput)
    return PlayerViewState.Playback(
      playlist = playlist,
      playerState = playerState,
      tracks = playerInput.tracks,
      currentTrackIndex = getCurrentTrackIndex(playerState, playerInput),
      currentTrackProgress = currentTrackProgress(playerState, currentTrackPositionMs),
      currentTrackImageBitmap = currentTrackImageBitmap,
      controlActions = controlActions,
      playbackActions =
        playerViewPlaybackActions(
          currentPlaylist = playlist,
          toggleCurrentPlayback = controlActions::onTogglePlay,
        ),
    )
  }

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
    toggleCurrentPlayback: () -> Unit,
  ): PlayerViewPlaybackActions =
    object : PlayerViewPlaybackActions {
      override fun start(playlist: Playlist) {
        if (playlist != currentPlaylist) startNewPlaylistPlayback(playlist)
        else toggleCurrentPlayback()
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
    viewModelScope.launch { playbackRepository.updatePlaybackPlaylist(playlist) }
  }

  private fun cancelPlayback() {
    viewModelScope.launch { playbackRepository.clear() }
  }

  private fun playerViewControlActions(
    playerState: PlayerState,
    playerInput: PlayerInput,
  ): PlayerViewControlActions =
    object : PlayerViewControlActions {
      override fun onTogglePlay() {
        when (playerState) {
          PlayerState.Idle -> {
            enqueue(playerInput)
          }
          is PlayerState.Enqueued -> {
            if (playerState.isPlaying) playerConnection.pause() else playerConnection.play()
          }
          is PlayerState.Error -> {
            return
          }
        }
      }

      override fun playPrevious() {
        when (playerState) {
          PlayerState.Idle -> {
            val start = playerInput.start
            enqueue(
              playerInput.copy(
                start =
                  start.copy(
                    trackIndex = (start.trackIndex - 1).coerceAtLeast(0),
                    trackPositionMs = 0L,
                    autoPlay = true,
                  )
              )
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
            val start = playerInput.start
            enqueue(
              playerInput.copy(
                start =
                  start.copy(
                    trackIndex = (start.trackIndex + 1).coerceAtMost(playerInput.tracks.lastIndex),
                    trackPositionMs = 0L,
                    autoPlay = true,
                  )
              )
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
          playbackRepository.updatePlaybackTrack(
            trackIndex = playerState.currentTrackIndex,
            trackPositionMs =
              playerState.currentTrack.positionMsOf(playbackState.currentTrackProgress),
          )
        }
      }
      is PlayerState.Error -> {
        // TODO: error handling
      }
    }
  }

  private fun getCurrentTrackArtworkUrl(playerState: PlayerState, input: PlayerInput): String? =
    when (playerState) {
      PlayerState.Idle -> {
        input.artworkUrl
      }
      is PlayerState.Enqueued -> {
        playerState.currentTrack.artworkUrl
      }
      is PlayerState.Error -> {
        playerState.previousEnqueuedState?.currentTrack?.artworkUrl ?: input.artworkUrl
      }
    }

  private fun getCurrentTrackIndex(playerState: PlayerState, input: PlayerInput): Int =
    when (playerState) {
      PlayerState.Idle -> {
        input.start.trackIndex
      }
      is PlayerState.Enqueued -> {
        playerState.currentTrackIndex
      }
      is PlayerState.Error -> {
        playerState.previousEnqueuedState?.currentTrackIndex ?: input.start.trackIndex
      }
    }

  private fun enqueue(input: PlayerInput) {
    val (tracks, host, start) = input
    playerConnection.enqueue(
      tracks = tracks,
      host = "https://$host",
      startTrackIndex = start.trackIndex,
      startPositionMs = start.trackPositionMs,
    )
  }
}
