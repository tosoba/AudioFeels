package com.trm.audiofeels.ui.player

import com.trm.audiofeels.domain.model.Playlist

interface PlayerViewActions {
  fun startPlayback(playlist: Playlist) {}

  fun onTogglePlayClick() {}

  fun onPreviousClick() {}

  fun onNextClick() {}

  fun cancelClick() {}
}
