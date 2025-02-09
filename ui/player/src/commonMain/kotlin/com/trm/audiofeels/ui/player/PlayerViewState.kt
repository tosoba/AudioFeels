package com.trm.audiofeels.ui.player

import androidx.compose.ui.graphics.ImageBitmap
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

sealed interface PlayerViewState {
  val playerVisible: Boolean
    get() = this !is Invisible

  val currentTrackImageBitmap: ImageBitmap?

  val playbackActions: PlayerViewPlaybackActions

  data class Invisible(override val playbackActions: PlayerViewPlaybackActions) : PlayerViewState {
    override val currentTrackImageBitmap: ImageBitmap? = null
  }

  data class Loading(
    val playlist: Playlist,
    override val playbackActions: PlayerViewPlaybackActions,
  ) : PlayerViewState {
    override val currentTrackImageBitmap: ImageBitmap? = null
  }

  data class Playback(
    val playlist: Playlist,
    val playerState: PlayerState,
    val tracks: List<Track>,
    private val currentTrackIndex: Int,
    val currentTrackProgress: Double,
    // TODO: use a placeholder in case of no artwork
    override val currentTrackImageBitmap: ImageBitmap?,
    val controlActions: PlayerViewControlActions,
    override val playbackActions: PlayerViewPlaybackActions,
  ) : PlayerViewState {
    val currentTrack: Track?
      get() =
        if (playerState is PlayerState.Enqueued) playerState.currentTrack
        else tracks.getOrNull(currentTrackIndex)

    val canPlayPrevious: Boolean
      get() = currentTrackIndex > 0

    val canPlayNext: Boolean
      get() = currentTrackIndex < tracks.lastIndex
  }

  data class Error(
    val playlist: Playlist,
    override val playbackActions: PlayerViewPlaybackActions,
  ) : PlayerViewState {
    override val currentTrackImageBitmap: ImageBitmap? = null
  }
}
