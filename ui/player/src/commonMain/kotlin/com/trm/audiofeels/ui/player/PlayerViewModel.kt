package com.trm.audiofeels.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.PlatformContext
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.core.base.model.loadableStateFlowOf
import com.trm.audiofeels.core.base.model.map
import com.trm.audiofeels.core.base.util.RestartableStateFlow
import com.trm.audiofeels.core.base.util.restartableStateIn
import com.trm.audiofeels.core.base.util.roundTo
import com.trm.audiofeels.core.ui.compose.util.loadImageBitmapOrNull
import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.player.PlayerConnection
import com.trm.audiofeels.domain.repository.PlaybackRepository
import com.trm.audiofeels.domain.usecase.GetPlayerInputUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModel(
  private val playerConnection: PlayerConnection,
  private val getPlayerInputUseCase: GetPlayerInputUseCase,
  private val playbackRepository: PlaybackRepository,
  private val imageLoader: ImageLoader,
  private val platformContext: PlatformContext,
) : ViewModel() {
  val viewState: RestartableStateFlow<PlayerViewState> =
    playbackRepository
      .getPlaybackPlaylistFlow()
      .distinctUntilChangedBy { it?.id }
      .flatMapLatest { playlist ->
        playlist?.id?.let { playlistId ->
          loadableStateFlowOf { getPlayerInputUseCase(playlistId) }
            .onEach { input ->
              if (input is LoadableState.Success && input.value.start.autoPlay) {
                enqueue(input.value)
              }
            }
            .flatMapLatest { playerInput -> playerViewStatePlaybackFlow(playerInput, playlist) }
            .onEach { onPlayerViewStatePlayback(it) }
        } ?: flowOf(PlayerViewState.Idle).onEach { playerConnection.reset() }
      }
      .restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = PlayerViewState.Idle,
      )

  private fun playerViewStatePlaybackFlow(
    playerInput: LoadableState<PlayerInput>,
    playlist: Playlist,
  ): Flow<PlayerViewState.Playback> {
    val tracks = playerInput.map(PlayerInput::tracks)
    return when (playerInput) {
      is LoadableState.Success -> {
        var lastArtworkUrl: String? = null

        playerConnection.playerState
          .transformLatest { playerState ->
            val trackArtworkUrl = getTrackArtworkUrl(playerState, playerInput.value)
            if (trackArtworkUrl != lastArtworkUrl) {
              lastArtworkUrl = trackArtworkUrl
              emit(playerState to null)
            }
            emit(
              playerState to
                trackArtworkUrl?.let { artworkUrl ->
                  imageLoader.loadImageBitmapOrNull(artworkUrl, platformContext)
                }
            )
          }
          .combine(
            playerConnection.currentTrackPositionMs.distinctUntilChanged().onStart { emit(0L) }
          ) { (playerState, currentTrackImageBitmap), currentTrackPositionMs ->
            PlayerViewState.Playback(
              playlist = playlist,
              playerState = playerState,
              tracks = tracks,
              currentTrackProgress =
                when (playerState) {
                  is PlayerState.Enqueued -> {
                    currentTrackPositionMs.toDouble() /
                      playerState.currentTrack.duration.toDouble() /
                      1000.0
                  }
                  PlayerState.Idle,
                  is PlayerState.Error -> {
                    0.0
                  }
                }.roundTo(3),
              currentTrackImageBitmap = currentTrackImageBitmap,
              actions =
                playerViewActions(
                  currentPlaylist = playlist,
                  playerState = playerState,
                  playerInput = playerInput.value,
                ),
            )
          }
      }
      LoadableState.Loading,
      is LoadableState.Error -> {
        flowOf(
          PlayerViewState.Playback(
            playlist = playlist,
            tracks = tracks,
            actions = object : PlayerViewActions {},
          )
        )
      }
    }
  }

  private fun playerViewActions(
    currentPlaylist: Playlist,
    playerState: PlayerState,
    playerInput: PlayerInput,
  ): PlayerViewActions =
    object : PlayerViewActions {
      override fun startPlayback(playlist: Playlist) {
        if (playlist != currentPlaylist) {
          viewModelScope.launch { playbackRepository.updatePlaybackPlaylist(playlist) }
        } else {
          onTogglePlayClick()
        }
      }

      override fun onTogglePlayClick() {
        when (playerState) {
          PlayerState.Idle -> {
            enqueue(playerInput)
          }
          is PlayerState.Enqueued -> {
            if (playerState.isPlaying) playerConnection.pause() else playerConnection.play()
          }
          is PlayerState.Error -> {}
        }
      }

      override fun onPreviousClick() {
        playerConnection.playPrevious()
      }

      override fun onNextClick() {
        playerConnection.playNext()
      }

      override fun cancelClick() {
        viewModelScope.launch { playbackRepository.clear() }
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
              (playbackState.currentTrackProgress *
                  playerState.currentTrack.duration.toDouble() *
                  1000.0)
                .roundToLong(),
          )
        }
      }
      is PlayerState.Error -> {
        // TODO: error handling
      }
    }
  }

  private fun getTrackArtworkUrl(playerState: PlayerState, input: PlayerInput): String? =
    when (playerState) {
      PlayerState.Idle -> {
        input.artworkUrl
      }
      is PlayerState.Enqueued -> {
        playerState.currentTrack.artworkUrl
      }
      is PlayerState.Error -> {
        (playerState.previousState as? PlayerState.Enqueued)?.currentTrack?.artworkUrl
          ?: input.artworkUrl
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
