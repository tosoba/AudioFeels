package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.ParameterizedInputAction
import com.trm.audiofeels.core.base.model.InputAction
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.core.base.util.roundTo
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.PlaybackState
import com.trm.audiofeels.domain.model.PlayerError
import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.repository.VisualizationRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import io.github.aakira.napier.Napier
import kotlin.math.roundToLong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playerConnection: PlayerConnection,
  private val getPlayerInputUseCase: GetPlayerInputUseCase,
  private val playlistsRepository: PlaylistsRepository,
  private val visualizationRepository: VisualizationRepository,
  private val hostsRepository: HostsRepository,
) : ViewModel() {
  val requestRecordAudioPermission: StateFlow<Boolean> =
    flow { emit(!visualizationRepository.isPermissionPermanentlyDenied()) }
      .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = false)

  private val _audioData = MutableStateFlow<List<Float>?>(null)
  val audioData: StateFlow<List<Float>?> = _audioData.asStateFlow()
  private var audioDataJob: Job? = null

  fun onRecordAudioPermissionGranted() {
    viewModelScope.launch { visualizationRepository.savePermissionPermanentlyDenied(false) }
    audioDataJob =
      playerConnection.audioDataFlow
        .onEach { _audioData.value = it.takeIf(List<Float>::isNotEmpty) }
        .launchIn(viewModelScope)
  }

  fun onRecordAudioPermissionDenied() {
    cancelAudioData()
  }

  fun onRecordAudioPermissionDeniedPermanently() {
    viewModelScope.launch { visualizationRepository.savePermissionPermanentlyDenied(true) }
    cancelAudioData()
  }

  private fun cancelAudioData() {
    audioDataJob?.cancel()
    _audioData.value = null
  }

  val currentPlaylist: StateFlow<Playlist?> =
    playlistsRepository
      .getCurrentPlaylistFlow()
      .distinctUntilChanged()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), null)

  val playerViewState: RestartableStateFlow<PlayerViewState> =
    playlistsRepository
      .getCurrentPlaylistPlaybackFlow()
      .distinctUntilChangedBy { playback -> playback?.playlist?.id }
      .flatMapLatest { playback ->
        playback?.let(::playerViewStateFlow)
          ?: flowOf(playerInvisibleViewState()).onEach { playerConnection.reset() }
      }
      .onEach { if (it is PlayerViewState.Playback) onPlayerPlaybackViewState(it) }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = playerInvisibleViewState(),
      )

  private fun playerInvisibleViewState(): PlayerViewState.Invisible =
    PlayerViewState.Invisible(
      startPlaylistPlayback =
        ParameterizedInputAction(PlaylistPlaybackActionInput(), ::startPlaylistPlayback),
      startCarryOnPlaylistPlayback =
        ParameterizedInputAction(PlaylistPlaybackActionInput(), ::startCarryOnPlaylistPlayback),
      cancelPlayback = ::cancelPlayback,
    )

  private fun playerViewStateFlow(playback: PlaylistPlayback): Flow<PlayerViewState> =
    loadableStateFlowOf { getPlayerInputUseCase(playback.playlist.id) }
      .onStart { if (playback.autoPlay) playerConnection.reset() }
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

  private fun playerViewStateFlow(
    input: LoadableState<PlayerInput>,
    playback: PlaylistPlayback,
  ): Flow<PlayerViewState> =
    when (input) {
      LoadableState.Loading -> flowOf(playerLoadingViewState())
      is LoadableState.Idle -> playerPlaybackViewStateFlow(input.value, playback)
      is LoadableState.Error -> flowOf(playerErrorViewState(playback))
    }

  private fun playerLoadingViewState(): PlayerViewState.Loading =
    PlayerViewState.Loading(
      startPlaylistPlayback =
        ParameterizedInputAction(PlaylistPlaybackActionInput(), ::startPlaylistPlayback),
      startCarryOnPlaylistPlayback =
        ParameterizedInputAction(PlaylistPlaybackActionInput(), ::startCarryOnPlaylistPlayback),
      cancelPlayback = ::cancelPlayback,
    )

  private fun playerErrorViewState(playback: PlaylistPlayback): PlayerViewState.Error =
    PlayerViewState.Error(
      startPlaylistPlayback =
        ParameterizedInputAction(PlaylistPlaybackActionInput(), ::startPlaylistPlayback),
      startCarryOnPlaylistPlayback =
        ParameterizedInputAction(PlaylistPlaybackActionInput(), ::startCarryOnPlaylistPlayback),
      cancelPlayback = ::cancelPlayback,
      primaryControlState =
        playerPrimaryControlRetryAction(playlist = playback.playlist, clearHost = false),
    )

  private fun playerPlaybackViewStateFlow(
    input: PlayerInput,
    playback: PlaylistPlayback,
  ): Flow<PlayerViewState> =
    playerConnection.playerStateFlow.flatMapLatest { playerState ->
      playerConnection.currentTrackPositionMsFlow
        .distinctUntilChanged()
        .filter { it > 0L }
        .onStart { emit(playback.currentTrackPositionMs) }
        .map {
          playerPlaybackViewState(
            playerInput = input,
            playback = playback,
            playerState = playerState,
            currentTrackPositionMs =
              if (playerState is PlayerState.Idle) playback.currentTrackPositionMs else it,
          )
        }
    }

  private fun playerPlaybackViewState(
    playerInput: PlayerInput,
    playback: PlaylistPlayback,
    playerState: PlayerState,
    currentTrackPositionMs: Long,
  ): PlayerViewState.Playback {
    val trackPlaybackActionInput = TrackPlaybackActionInput(playerState, playerInput, playback)
    val togglePlayback = InputAction(trackPlaybackActionInput, ::togglePlayback)
    val playlistPlaybackActionInput = PlaylistPlaybackActionInput(playback.playlist, togglePlayback)
    return PlayerViewState.Playback(
      playlistId = playback.playlist.id,
      playerState = playerState,
      tracks = playerInput.tracks,
      currentTrackIndex = getCurrentTrackIndex(playerState, playback),
      currentTrackProgress =
        currentTrackProgress(playerInput, playback, playerState, currentTrackPositionMs),
      primaryControlState = primaryControlState(playback.playlist, playerState, togglePlayback),
      startPlaylistPlayback =
        ParameterizedInputAction(playlistPlaybackActionInput, ::startPlaylistPlayback),
      startCarryOnPlaylistPlayback =
        ParameterizedInputAction(playlistPlaybackActionInput, ::startCarryOnPlaylistPlayback),
      cancelPlayback = ::cancelPlayback,
      togglePlaylistFavourite = ::toggleCurrentPlaylistFavourite,
      playPreviousTrack = InputAction(trackPlaybackActionInput, ::playPreviousTrack),
      playNextTrack = InputAction(trackPlaybackActionInput, ::playNextTrack),
      playTrackAtIndex = ParameterizedInputAction(trackPlaybackActionInput, ::playTrackAtIndex),
      seekToProgress =
        ParameterizedInputAction(
          when (playerState) {
            PlayerState.Idle -> null
            is PlayerState.Enqueued -> playerState.currentTrack
            is PlayerState.Error -> null
          },
          ::seekToProgress,
        ),
    )
  }

  private fun primaryControlState(
    playlist: Playlist,
    playerState: PlayerState,
    togglePlayback: () -> Unit,
  ): PlayerPrimaryControlState =
    when (playerState) {
      PlayerState.Idle -> {
        PlayerPrimaryControlState.playAction(togglePlayback)
      }
      is PlayerState.Enqueued -> {
        when {
          playerState.playbackState == PlaybackState.BUFFERING -> {
            PlayerPrimaryControlState.Loading
          }
          playerState.isPlaying -> {
            PlayerPrimaryControlState.pauseAction(togglePlayback)
          }
          else -> {
            PlayerPrimaryControlState.playAction(togglePlayback)
          }
        }
      }
      is PlayerState.Error -> {
        playerPrimaryControlRetryAction(
          playlist = playlist,
          clearHost = playerState.error == PlayerError.INVALID_HOST_ERROR,
        )
      }
    }

  private fun playerPrimaryControlRetryAction(
    playlist: Playlist,
    clearHost: Boolean,
  ): PlayerPrimaryControlState.Action =
    PlayerPrimaryControlState.retryAction {
      viewModelScope
        .launch {
          if (clearHost) hostsRepository.clearHost()
          suspendStartNewPlaylistPlayback(playlist = playlist, carryOn = true)
        }
        .invokeOnCompletion { playerViewState.restart() }
    }

  private fun currentTrackProgress(
    playerInput: PlayerInput,
    playback: PlaylistPlayback,
    playerState: PlayerState,
    currentTrackPositionMs: Long,
  ): Double =
    when (playerState) {
      PlayerState.Idle -> {
        playerInput.tracks.getOrNull(playback.currentTrackIndex)?.duration?.toDouble()?.let {
          playback.currentTrackPositionMs.toDouble() / it / 1000.0
        } ?: 0.0
      }
      is PlayerState.Enqueued -> {
        currentTrackPositionMs.toDouble() / playerState.currentTrack.duration.toDouble() / 1000.0
      }
      is PlayerState.Error -> {
        0.0
      }
    }.roundTo(3)

  private fun onPlayerPlaybackViewState(state: PlayerViewState.Playback) {
    Napier.d(tag = "PLAYER_STATE", message = state.playerState.toString())
    when (val playerState = state.playerState) {
      is PlayerState.Enqueued -> {
        viewModelScope.launch {
          playlistsRepository.updateCurrentPlaylist(
            id = state.playlistId,
            currentTrackIndex = playerState.currentTrackIndex,
            currentTrackPositionMs =
              playerState.currentTrack.positionMsOf(state.currentTrackProgress),
          )
        }
      }
      is PlayerState.Idle,
      is PlayerState.Error -> {
        return
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

  private fun startPlaylistPlayback(input: PlaylistPlaybackActionInput, playlist: Playlist) {
    if (playlist.id != input.currentPlaylist?.id) {
      startNewPlaylistPlayback(playlist = playlist, carryOn = false)
    } else {
      input.togglePlayback?.invoke()
    }
  }

  private fun startCarryOnPlaylistPlayback(
    input: PlaylistPlaybackActionInput,
    carryOnPlaylist: CarryOnPlaylist,
  ) {
    if (carryOnPlaylist.playlist.id != input.currentPlaylist?.id) {
      startNewPlaylistPlayback(playlist = carryOnPlaylist.playlist, carryOn = true)
    } else {
      input.togglePlayback?.invoke()
    }
  }

  private fun startNewPlaylistPlayback(playlist: Playlist, carryOn: Boolean): Job =
    viewModelScope.launch { suspendStartNewPlaylistPlayback(playlist, carryOn) }

  private suspend fun suspendStartNewPlaylistPlayback(playlist: Playlist, carryOn: Boolean) {
    playlistsRepository.setNewCurrentPlaylist(playlist, carryOn)
  }

  private fun cancelPlayback() {
    viewModelScope.launch { playlistsRepository.clearCurrentPlaylist() }
  }

  private fun toggleCurrentPlaylistFavourite() {
    viewModelScope.launch { playlistsRepository.toggleCurrentPlaylistFavourite() }
  }

  private fun togglePlayback(input: TrackPlaybackActionInput) {
    val (playerState, playerInput, playback) = input
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

  private fun playPreviousTrack(input: TrackPlaybackActionInput) {
    val (playerState, playerInput, playback) = input
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
        playerState.previousEnqueuedState?.let { playerConnection.playPrevious() }
      }
    }
  }

  private fun playNextTrack(input: TrackPlaybackActionInput) {
    val (playerState, playerInput, playback) = input
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
        playerState.previousEnqueuedState?.let { playerConnection.playNext() }
      }
    }
  }

  private fun playTrackAtIndex(input: TrackPlaybackActionInput, index: Int) {
    val (playerState, playerInput, playback) = input
    when (playerState) {
      PlayerState.Idle -> {
        if (playback.currentTrackIndex != index) {
          playerConnection.enqueue(
            input = playerInput,
            startTrackIndex = index.coerceIn(0..playerInput.tracks.lastIndex),
            startPositionMs = 0L,
          )
        }
      }
      is PlayerState.Enqueued -> {
        if (playerState.currentTrackIndex != index) {
          playerConnection.playAtIndex(index)
        }
      }
      is PlayerState.Error -> {
        playerState.previousEnqueuedState?.let { playerConnection.playAtIndex(index) }
      }
    }
  }

  private fun seekToProgress(currentTrack: Track?, progress: Float) {
    currentTrack
      ?.duration
      ?.toFloat()
      ?.let { it * progress * 1000f }
      ?.roundToLong()
      ?.let(playerConnection::seekTo)
  }

  private data class PlaylistPlaybackActionInput(
    val currentPlaylist: Playlist? = null,
    val togglePlayback: (() -> Unit)? = null,
  )

  private data class TrackPlaybackActionInput(
    val playerState: PlayerState,
    val playerInput: PlayerInput,
    val playback: PlaylistPlayback,
  )
}
