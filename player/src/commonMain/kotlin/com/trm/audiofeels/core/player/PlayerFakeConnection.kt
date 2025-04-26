package com.trm.audiofeels.core.player

import com.trm.audiofeels.domain.model.PlaybackState
import com.trm.audiofeels.domain.model.PlayerConstants
import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.player.PlayerConnection
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive

class PlayerFakeConnection : PlayerConnection {
  private val _playerStateFlow = MutableStateFlow<PlayerState>(PlayerState.Idle)
  override val playerStateFlow: Flow<PlayerState> = _playerStateFlow.asStateFlow()

  private val currentTracks = mutableListOf<Track>()
  private var currentTrackPositionMs: Long = PlayerConstants.MIN_TRACK_POSITION_MS

  private val PlayerState.Enqueued.previousTrackIndex: Int
    get() = if (currentTrackIndex > 0) currentTrackIndex - 1 else currentTracks.lastIndex

  private val PlayerState.Enqueued.nextTrackIndex: Int
    get() = if (currentTrackIndex < currentTracks.lastIndex) currentTrackIndex + 1 else 0

  override val currentTrackPositionMsFlow: Flow<Long> = flow {
    while (currentCoroutineContext().isActive) {
      when (val playerState = _playerStateFlow.value) {
        is PlayerState.Enqueued -> {
          if (playerState.isPlaying) {
            if (currentTrackPositionMs < playerState.currentTrack.duration) {
              emit(++currentTrackPositionMs)
            } else {
              _playerStateFlow.update { state ->
                if (state is PlayerState.Enqueued) {
                  enqueuedWithCurrentTrackAt(state.nextTrackIndex).also {
                    currentTrackPositionMs = 0
                    emit(currentTrackPositionMs)
                  }
                } else {
                  state
                }
              }
            }
          } else {
            emit(currentTrackPositionMs)
          }
        }
        else -> {
          emit(PlayerConstants.MIN_TRACK_POSITION_MS)
        }
      }
      delay(PlayerConstants.TRACK_POSITION_UPDATE_INTERVAL_MS)
    }
  }

  override val audioDataFlow: Flow<List<Float>> = emptyFlow()

  override fun play() {
    _playerStateFlow.update { state ->
      if (state is PlayerState.Enqueued) state.copy(isPlaying = true) else state
    }
  }

  override fun pause() {
    _playerStateFlow.update { state ->
      if (state is PlayerState.Enqueued) state.copy(isPlaying = false) else state
    }
  }

  override fun playPrevious() {
    _playerStateFlow.update { state ->
      when (state) {
        PlayerState.Idle -> {
          state
        }
        is PlayerState.Enqueued -> {
          enqueuedWithCurrentTrackAt(state.previousTrackIndex).also { currentTrackPositionMs = 0 }
        }
        is PlayerState.Error -> {
          state.previousEnqueuedState
            ?.let { previousState -> enqueuedWithCurrentTrackAt(previousState.previousTrackIndex) }
            ?.also { currentTrackPositionMs = PlayerConstants.MIN_TRACK_POSITION_MS } ?: state
        }
      }
    }
  }

  override fun playNext() {
    _playerStateFlow.update { state ->
      when (state) {
        PlayerState.Idle -> {
          state
        }
        is PlayerState.Enqueued -> {
          enqueuedWithCurrentTrackAt(state.nextTrackIndex).also { currentTrackPositionMs = 0 }
        }
        is PlayerState.Error -> {
          state.previousEnqueuedState
            ?.let { previousState -> enqueuedWithCurrentTrackAt(previousState.nextTrackIndex) }
            ?.also { currentTrackPositionMs = PlayerConstants.MIN_TRACK_POSITION_MS } ?: state
        }
      }
    }
  }

  override fun playAtIndex(index: Int) {
    require(index in currentTracks.indices)

    currentTrackPositionMs = PlayerConstants.MIN_TRACK_POSITION_MS

    _playerStateFlow.update { enqueuedWithCurrentTrackAt(index) }
  }

  override fun seekTo(positionMs: Long) {
    when (val state = _playerStateFlow.value) {
      is PlayerState.Enqueued -> {
        require(positionMs in PlayerConstants.MIN_TRACK_POSITION_MS..state.currentTrack.duration)
        currentTrackPositionMs = positionMs
      }
      else -> {}
    }
  }

  override fun enqueue(input: PlayerInput, startTrackIndex: Int, startPositionMs: Long) {
    require(startTrackIndex in input.tracks.indices)
    require(
      startPositionMs in
        PlayerConstants.MIN_TRACK_POSITION_MS..input.tracks[startTrackIndex].duration
    )

    currentTracks.clear()
    currentTracks.addAll(input.tracks)
    currentTrackPositionMs = startPositionMs

    _playerStateFlow.update { enqueuedWithCurrentTrackAt(startTrackIndex) }
  }

  private fun enqueuedWithCurrentTrackAt(index: Int): PlayerState.Enqueued =
    PlayerState.Enqueued(
      currentTrack = currentTracks[index],
      currentTrackIndex = index,
      playbackState = PlaybackState.READY,
      isPlaying = true,
    )

  override fun reset() {
    currentTracks.clear()
    currentTrackPositionMs = PlayerConstants.MIN_TRACK_POSITION_MS

    _playerStateFlow.update { PlayerState.Idle }
  }
}
