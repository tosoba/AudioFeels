package com.trm.audiofeels.ui.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.PlaybackState
import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.PlaylistPlayback
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import com.trm.audiofeels.domain.repository.VisualizationRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playerConnection: PlayerConnection,
  private val getPlayerInputUseCase: GetPlayerInputUseCase,
  private val playlistsRepository: PlaylistsRepository,
  private val visualizationRepository: VisualizationRepository,
  private val imageLoader: ImageLoader,
  private val platformContext: PlatformContext,
) : ViewModel() {
  val handleRecordAudioPermission: StateFlow<Boolean> =
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

  val viewState: RestartableStateFlow<PlayerViewState> =
    playlistsRepository
      .getCurrentPlaylistPlaybackFlow()
      .distinctUntilChangedBy { playback -> playback?.playlist?.id }
      .flatMapLatest { playback ->
        playback?.let(::playerViewStateFlow)
          ?: flowOf(invisibleViewState()).onEach { playerConnection.reset() }
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = invisibleViewState(),
      )

  private fun invisibleViewState(): PlayerViewState.Invisible =
    PlayerViewState.Invisible(
      startPlaylistPlayback = StartPlaylistPlayback(),
      startCarryOnPlaylistPlayback = StartCarryOnPlaylistPlayback(),
      cancelPlayback = ::cancelPlayback,
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
            startPlaylistPlayback = StartPlaylistPlayback(),
            startCarryOnPlaylistPlayback = StartCarryOnPlaylistPlayback(),
            cancelPlayback = ::cancelPlayback,
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
            startPlaylistPlayback = StartPlaylistPlayback(),
            startCarryOnPlaylistPlayback = StartCarryOnPlaylistPlayback(),
            cancelPlayback = ::cancelPlayback,
            primaryControlState = retryAction(playback.playlist),
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
      playerConnection.playerStateFlow.onEach {
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
      playerConnection.currentTrackPositionMsFlow.distinctUntilChanged().onStart { emit(0L) },
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
    val togglePlayback = TogglePlayback(PlaybackActionArguments(playerState, playerInput, playback))
    return PlayerViewState.Playback(
      playlist = playback.playlist,
      playerState = playerState,
      tracks = playerInput.tracks,
      currentTrackIndex = getCurrentTrackIndex(playerState, playback),
      currentTrackProgress = currentTrackProgress(playerState, currentTrackPositionMs),
      currentTrackImageBitmap = currentTrackImageBitmap,
      primaryControlState = primaryControlState(playback.playlist, playerState, togglePlayback),
      startPlaylistPlayback = StartPlaylistPlayback(playback.playlist, togglePlayback),
      startCarryOnPlaylistPlayback =
        StartCarryOnPlaylistPlayback(playback.playlist, togglePlayback),
      cancelPlayback = ::cancelPlayback,
      playPreviousTrack =
        PlayPreviousTrackAction(PlaybackActionArguments(playerState, playerInput, playback)),
      playNextTrack =
        PlayNextTrackAction(PlaybackActionArguments(playerState, playerInput, playback)),
      playTrackAtIndex =
        PlayAtIndexTrackAction(PlaybackActionArguments(playerState, playerInput, playback)),
    )
  }

  private fun primaryControlState(
    playlist: Playlist,
    playerState: PlayerState,
    togglePlayback: TogglePlayback,
  ): PlayerViewState.PrimaryControlState =
    when (playerState) {
      PlayerState.Idle -> {
        playAction(togglePlayback)
      }
      is PlayerState.Enqueued -> {
        when {
          playerState.playbackState == PlaybackState.BUFFERING -> {
            PlayerViewState.PrimaryControlState.Loading
          }
          playerState.isPlaying -> {
            pauseAction(togglePlayback)
          }
          else -> {
            playAction(togglePlayback)
          }
        }
      }
      is PlayerState.Error -> {
        retryAction(playlist)
      }
    }

  private fun pauseAction(togglePlayback: TogglePlayback) =
    PlayerViewState.PrimaryControlState.Action(
      imageVector = Icons.Filled.Pause,
      contentDescription = "Pause",
      action = togglePlayback,
    )

  private fun playAction(togglePlayback: TogglePlayback) =
    PlayerViewState.PrimaryControlState.Action(
      imageVector = Icons.Filled.PlayArrow,
      contentDescription = "Play",
      action = togglePlayback,
    )

  private fun retryAction(playlist: Playlist) =
    PlayerViewState.PrimaryControlState.Action(
      imageVector = Icons.Filled.Refresh,
      contentDescription = "Retry",
      action = {
        startNewPlaylistPlayback(playlist = playlist, carryOn = true).invokeOnCompletion {
          viewState.restart()
        }
      },
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

  private fun onPlayerViewStatePlayback(playbackState: PlayerViewState.Playback) {
    Napier.d(tag = "PLAYER_STATE", message = playbackState.playerState.toString())
    when (val playerState = playbackState.playerState) {
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
      is PlayerState.Idle,
      is PlayerState.Error -> {
        return
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

  private inner class StartPlaylistPlayback(
    private val currentPlaylist: Playlist? = null,
    private val togglePlayback: TogglePlayback? = null,
  ) : (Playlist) -> Unit {
    override fun invoke(playlist: Playlist) {
      if (playlist != currentPlaylist) {
        startNewPlaylistPlayback(playlist = playlist, carryOn = false)
      } else {
        togglePlayback?.invoke()
      }
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is StartPlaylistPlayback) return false
      if (currentPlaylist != other.currentPlaylist) return false
      if (togglePlayback != other.togglePlayback) return false
      return true
    }

    override fun hashCode(): Int {
      var result = currentPlaylist?.hashCode() ?: 0
      result = 31 * result + togglePlayback.hashCode()
      return result
    }
  }

  private inner class StartCarryOnPlaylistPlayback(
    private val currentPlaylist: Playlist? = null,
    private val togglePlayback: TogglePlayback? = null,
  ) : (CarryOnPlaylist) -> Unit {
    override fun invoke(carryOnPlaylist: CarryOnPlaylist) {
      if (carryOnPlaylist.playlist != currentPlaylist) {
        startNewPlaylistPlayback(playlist = carryOnPlaylist.playlist, carryOn = false)
      } else {
        togglePlayback?.invoke()
      }
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is StartCarryOnPlaylistPlayback) return false
      if (currentPlaylist != other.currentPlaylist) return false
      if (togglePlayback != other.togglePlayback) return false
      return true
    }

    override fun hashCode(): Int {
      var result = currentPlaylist?.hashCode() ?: 0
      result = 31 * result + togglePlayback.hashCode()
      return result
    }
  }

  private fun startNewPlaylistPlayback(playlist: Playlist, carryOn: Boolean): Job =
    viewModelScope.launch {
      playlistsRepository.setNewCurrentPlaylist(playlist = playlist, carryOn = carryOn)
    }

  private fun cancelPlayback() {
    viewModelScope.launch { playlistsRepository.clearCurrentPlaylist() }
  }

  private inner class TogglePlayback(private val arguments: PlaybackActionArguments) : () -> Unit {
    override fun invoke() {
      val (playerState, playerInput, playback) = arguments
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

    override fun equals(other: Any?): Boolean =
      when {
        this === other -> true
        other !is TogglePlayback -> false
        else -> arguments == other.arguments
      }

    override fun hashCode(): Int = arguments.hashCode()
  }

  private inner class PlayPreviousTrackAction(private val arguments: PlaybackActionArguments) :
    () -> Unit {
    override fun invoke() {
      val (playerState, playerInput, playback) = arguments
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

    override fun equals(other: Any?): Boolean =
      when {
        this === other -> true
        other !is PlayPreviousTrackAction -> false
        else -> arguments == other.arguments
      }

    override fun hashCode(): Int = arguments.hashCode()
  }

  private inner class PlayNextTrackAction(private val arguments: PlaybackActionArguments) :
    () -> Unit {
    override fun invoke() {
      val (playerState, playerInput, playback) = arguments
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
          playerConnection.playPrevious()
        }
        is PlayerState.Error -> {
          return
        }
      }
    }

    override fun equals(other: Any?): Boolean =
      when {
        this === other -> true
        other !is PlayNextTrackAction -> false
        else -> arguments == other.arguments
      }

    override fun hashCode(): Int = arguments.hashCode()
  }

  private inner class PlayAtIndexTrackAction(private val arguments: PlaybackActionArguments) :
    (Int) -> Unit {
    override fun invoke(index: Int) {
      val (playerState, playerInput, playback) = arguments
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
          return
        }
      }
    }

    override fun equals(other: Any?): Boolean =
      when {
        this === other -> true
        other !is PlayAtIndexTrackAction -> false
        else -> arguments == other.arguments
      }

    override fun hashCode(): Int = arguments.hashCode()
  }

  private data class PlaybackActionArguments(
    val playerState: PlayerState,
    val playerInput: PlayerInput,
    val playback: PlaylistPlayback,
  )
}
