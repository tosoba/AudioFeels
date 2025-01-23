package com.trm.audiofeels.ui.player

import com.trm.audiofeels.domain.model.Playlist

interface PlayerViewPlaybackActions {
  fun start(playlist: Playlist)

  fun cancel()
}
