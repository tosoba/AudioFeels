package com.trm.audiofeels.core.player

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

internal object PlayerSessionCallback : MediaLibrarySession.Callback {
  override fun onAddMediaItems(
    mediaSession: MediaSession,
    controller: MediaSession.ControllerInfo,
    mediaItems: List<MediaItem>,
  ): ListenableFuture<List<MediaItem>> =
    Futures.immediateFuture(
      mediaItems.map { mediaItem ->
        mediaItem.buildUpon().setUri(mediaItem.requestMetadata.mediaUri).build()
      }
    )
}
