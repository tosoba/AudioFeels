package com.trm.audiofeels.ui.player

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import org.jetbrains.compose.resources.StringResource

sealed interface PlayerViewState {
  val playerVisible: Boolean
    get() = this !is Invisible

  val currentTrackImageBitmap: LoadableState<ImageBitmap?>

  val startPlaylistPlayback: (Playlist) -> Unit
  val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit
  val cancelPlayback: () -> Unit

  val primaryControlState: PrimaryControlState

  val isPlaying: Boolean
    get() = this is Playback && playerState is PlayerState.Enqueued && playerState.isPlaying

  sealed interface PrimaryControlState {
    data object Loading : PrimaryControlState

    data class Action(
      val imageVector: ImageVector,
      val contentDescription: StringResource?,
      val action: () -> Unit,
    ) : PrimaryControlState
  }

  data class Invisible(
    override val startPlaylistPlayback: (Playlist) -> Unit,
    override val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit,
    override val cancelPlayback: () -> Unit,
  ) : PlayerViewState {
    override val currentTrackImageBitmap: LoadableState.Idle<ImageBitmap?> =
      LoadableState.Idle(null)

    override val primaryControlState: PrimaryControlState.Loading = PrimaryControlState.Loading
  }

  data class Loading(
    override val startPlaylistPlayback: (Playlist) -> Unit,
    override val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit,
    override val cancelPlayback: () -> Unit,
  ) : PlayerViewState {
    override val currentTrackImageBitmap: LoadableState.Idle<ImageBitmap?> =
      LoadableState.Idle(null)

    override val primaryControlState: PrimaryControlState.Loading = PrimaryControlState.Loading
  }

  data class Playback(
    val playlistId: String,
    val playerState: PlayerState,
    val tracks: List<Track>,
    val currentTrackIndex: Int,
    val currentTrackProgress: Double,
    override val currentTrackImageBitmap: LoadableState<ImageBitmap?>,
    override val primaryControlState: PrimaryControlState,
    override val startPlaylistPlayback: (Playlist) -> Unit,
    override val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit,
    override val cancelPlayback: () -> Unit,
    val togglePlaylistFavourite: () -> Unit,
    val playPreviousTrack: () -> Unit,
    val playNextTrack: () -> Unit,
    val playTrackAtIndex: (Int) -> Unit,
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
    override val primaryControlState: PrimaryControlState.Action,
    override val startPlaylistPlayback: (Playlist) -> Unit,
    override val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit,
    override val cancelPlayback: () -> Unit,
  ) : PlayerViewState {
    override val currentTrackImageBitmap: LoadableState.Idle<ImageBitmap?> =
      LoadableState.Idle(null)
  }
}
