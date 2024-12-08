package com.trm.audiofeels.core.player

import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

class PlayerService : MediaLibraryService() {
  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
    TODO("Not yet implemented")
  }
}
