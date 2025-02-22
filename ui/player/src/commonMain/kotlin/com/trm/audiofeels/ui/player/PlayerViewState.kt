package com.trm.audiofeels.ui.player

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

sealed interface PlayerViewState {
  val playerVisible: Boolean
    get() = this !is Invisible

  val currentTrackImageBitmap: LoadableState<ImageBitmap?>

  val playbackActions: PlayerViewPlaybackActions
  val primaryControlState: PrimaryControlState

  sealed interface PrimaryControlState {
    data object Loading : PrimaryControlState

    data class Action(
      val imageVector: ImageVector,
      val contentDescription: String?,
      val action: () -> Unit,
    ) : PrimaryControlState
  }

  data class Invisible(override val playbackActions: PlayerViewPlaybackActions) : PlayerViewState {
    override val currentTrackImageBitmap: LoadableState.Idle<ImageBitmap?> =
      LoadableState.Idle(null)

    override val primaryControlState: PrimaryControlState.Loading = PrimaryControlState.Loading
  }

  data class Loading(
    val playlist: Playlist,
    override val playbackActions: PlayerViewPlaybackActions,
  ) : PlayerViewState {
    override val currentTrackImageBitmap: LoadableState.Idle<ImageBitmap?> =
      LoadableState.Idle(null)

    override val primaryControlState: PrimaryControlState.Loading = PrimaryControlState.Loading
  }

  data class Playback(
    val playlist: Playlist,
    val playerState: PlayerState,
    val tracks: List<Track>,
    val currentTrackIndex: Int,
    val currentTrackProgress: Double,
    override val currentTrackImageBitmap: LoadableState<ImageBitmap?>,
    override val primaryControlState: PrimaryControlState,
    override val playbackActions: PlayerViewPlaybackActions,
    val trackActions: PlayerViewTrackActions,
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
    override val primaryControlState: PrimaryControlState.Action,
    override val playbackActions: PlayerViewPlaybackActions,
  ) : PlayerViewState {
    override val currentTrackImageBitmap: LoadableState.Idle<ImageBitmap?> =
      LoadableState.Idle(null)
  }
}
