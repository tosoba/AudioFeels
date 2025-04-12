package com.trm.audiofeels.ui.player

import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.PlayerState
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track

sealed interface PlayerViewState {
  val primaryControlState: PlayerPrimaryControlState
  val startPlaylistPlayback: (Playlist) -> Unit
  val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit
  val cancelPlayback: () -> Unit

  data class Invisible(
    override val startPlaylistPlayback: (Playlist) -> Unit,
    override val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit,
    override val cancelPlayback: () -> Unit,
  ) : PlayerViewState {
    override val primaryControlState: PlayerPrimaryControlState.Loading =
      PlayerPrimaryControlState.Loading
  }

  data class Loading(
    override val startPlaylistPlayback: (Playlist) -> Unit,
    override val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit,
    override val cancelPlayback: () -> Unit,
  ) : PlayerViewState {
    override val primaryControlState: PlayerPrimaryControlState.Loading =
      PlayerPrimaryControlState.Loading
  }

  data class Playback(
    val playlistId: String,
    val playerState: PlayerState,
    val tracks: List<Track>,
    val currentTrackIndex: Int,
    val currentTrackProgress: Double,
    override val primaryControlState: PlayerPrimaryControlState,
    override val startPlaylistPlayback: (Playlist) -> Unit,
    override val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit,
    override val cancelPlayback: () -> Unit,
    val togglePlaylistFavourite: () -> Unit,
    val playPreviousTrack: () -> Unit,
    val playNextTrack: () -> Unit,
    val playTrackAtIndex: (Int) -> Unit,
    val seekToProgress: (Float) -> Unit,
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
    override val primaryControlState: PlayerPrimaryControlState.Action,
    override val startPlaylistPlayback: (Playlist) -> Unit,
    override val startCarryOnPlaylistPlayback: (CarryOnPlaylist) -> Unit,
    override val cancelPlayback: () -> Unit,
  ) : PlayerViewState
}
