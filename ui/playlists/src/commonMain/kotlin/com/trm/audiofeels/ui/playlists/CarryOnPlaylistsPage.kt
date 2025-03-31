package com.trm.audiofeels.ui.playlists

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import com.trm.audiofeels.core.base.model.LoadableState
import com.trm.audiofeels.domain.model.CarryOnPlaylist
import dev.chrisbanes.haze.HazeState

@Composable
fun CarryOnPlaylistsPage(
  playlists: LoadableState<List<CarryOnPlaylist>>,
  bottomSpacerHeight: Dp,
  onPlaylistClick: (CarryOnPlaylist) -> Unit,
  onNavigationIconClick: () -> Unit,
) {
  val hazeState = remember(::HazeState)
}
