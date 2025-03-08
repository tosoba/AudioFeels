package com.trm.audiofeels.core.player

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

internal class PlayerSessionCallback(private val player: ExoPlayer) : MediaLibrarySession.Callback {
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

  @OptIn(UnstableApi::class)
  override fun onConnect(
    session: MediaSession,
    controller: MediaSession.ControllerInfo,
  ): MediaSession.ConnectionResult =
    MediaSession.ConnectionResult.AcceptedResultBuilder(session)
      .setAvailableSessionCommands(
        MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
          .add(SessionCommand(ACTION_AUDIO_SESSION_ID, Bundle.EMPTY))
          .build()
      )
      .build()

  @OptIn(UnstableApi::class)
  override fun onCustomCommand(
    session: MediaSession,
    controller: MediaSession.ControllerInfo,
    customCommand: SessionCommand,
    args: Bundle,
  ): ListenableFuture<SessionResult> =
    when (customCommand.customAction) {
      ACTION_AUDIO_SESSION_ID -> {
        Futures.immediateFuture(
          SessionResult(
            SessionResult.RESULT_SUCCESS,
            bundleOf(EXTRA_AUDIO_SESSION_ID to player.audioSessionId),
          )
        )
      }
      else -> {
        super.onCustomCommand(session, controller, customCommand, args)
      }
    }

  companion object {
    private const val PREFIX = "com.trm.audiofeels.core.player"

    const val ACTION_AUDIO_SESSION_ID = "$PREFIX.action.AUDIO_SESSION_ID"
    const val EXTRA_AUDIO_SESSION_ID = "$PREFIX.extra.AUDIO_SESSION_ID"
  }
}
