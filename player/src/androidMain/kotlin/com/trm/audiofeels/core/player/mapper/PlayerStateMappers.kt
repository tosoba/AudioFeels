package com.trm.audiofeels.core.player.mapper

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.HttpDataSource.HttpDataSourceException
import com.trm.audiofeels.domain.model.PlaybackState
import com.trm.audiofeels.domain.model.PlayerError
import com.trm.audiofeels.domain.model.PlayerState

internal fun Player.toState(): PlayerState =
  currentMediaItem?.let { mediaItem ->
    PlayerState.Enqueued(
      currentTrack = mediaItem.toTrack(),
      currentTrackIndex = currentMediaItemIndex,
      playbackState =
        when (playbackState) {
          Player.STATE_IDLE -> PlaybackState.IDLE
          Player.STATE_BUFFERING -> PlaybackState.BUFFERING
          Player.STATE_READY -> PlaybackState.READY
          Player.STATE_ENDED -> PlaybackState.ENDED
          else -> throw IllegalArgumentException()
        },
      isPlaying = isPlaying,
      trackDurationMs = duration,
    )
  } ?: PlayerState.Idle

internal fun PlaybackException.toPlayerError(): PlayerError =
  when (cause) {
    is HttpDataSource.InvalidResponseCodeException -> PlayerError.INVALID_HOST_ERROR
    is HttpDataSourceException -> PlayerError.CONNECTION_ERROR
    else -> PlayerError.OTHER_ERROR
  }
