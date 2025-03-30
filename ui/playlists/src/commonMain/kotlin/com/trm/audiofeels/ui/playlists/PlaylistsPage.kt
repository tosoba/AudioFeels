package com.trm.audiofeels.ui.playlists

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.trm.audiofeels.domain.model.Playlist

@Composable
fun PlaylistsPage(
  playlistsViewModel: PlaylistsViewModel,
  bottomSpacerHeight: Dp,
  onPlaylistClick: (Playlist) -> Unit,
  onNavigationIconClick: () -> Unit,
) {}
