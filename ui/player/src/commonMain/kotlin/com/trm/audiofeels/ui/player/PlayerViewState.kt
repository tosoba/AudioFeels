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
    val playerState: PlayerState = PlayerState.Idle,
    val tracks: List<Track> = emptyList(),
    val currentTrackProgress: Double = 0.0,
    override val currentTrackImageBitmap: ImageBitmap? =
      null, // TODO: use a placeholder in case of no artwork
    val controlActions: PlayerViewControlActions,
    override val playbackActions: PlayerViewPlaybackActions,
  ) : PlayerViewState

  data class Error(
    val playlist: Playlist,
    override val playbackActions: PlayerViewPlaybackActions,
  ) : PlayerViewState {
    override val currentTrackImageBitmap: ImageBitmap? = null
  }
}
