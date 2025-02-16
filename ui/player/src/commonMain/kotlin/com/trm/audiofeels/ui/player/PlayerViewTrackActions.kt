package com.trm.audiofeels.ui.player

interface PlayerViewTrackActions {
  fun playPrevious()

  fun playNext()

  fun playAtIndex(index: Int)
}
