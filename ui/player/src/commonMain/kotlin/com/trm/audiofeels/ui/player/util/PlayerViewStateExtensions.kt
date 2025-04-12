package com.trm.audiofeels.ui.player.util

import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.ui.player.PlayerViewState
import com.trm.audiofeels.ui.player.PlayerViewState.Invisible
import com.trm.audiofeels.ui.player.PlayerViewState.Playback

val PlayerViewState.playerVisible: Boolean
  get() = this !is Invisible

val PlayerViewState.isPlaying: Boolean
  get() = this is Playback && playerState is PlayerState.Enqueued && playerState.isPlaying

val PlayerViewState.currentTrackArtworkUrl: String?
  get() = (this as? Playback)?.currentTrack?.artworkUrl

internal val PlayerViewState.currentTrackProgressOrZero: Float
  get() = (this as? Playback)?.currentTrackProgress?.toFloat() ?: 0f
