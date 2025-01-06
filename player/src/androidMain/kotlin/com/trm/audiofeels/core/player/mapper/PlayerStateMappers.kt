package com.trm.audiofeels.core.player.mapper

import androidx.media3.common.Player
import com.trm.audiofeels.domain.model.PlaybackState
import com.trm.audiofeels.domain.model.PlayerState

internal fun Player.toState(): PlayerState =
  currentMediaItem?.let { mediaItem ->
    PlayerState.Initialized(
      currentTrack = mediaItem.toTrack(),
      currentTrackIndex = currentMediaItemIndex,
      tracksCount = mediaItemCount,
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
