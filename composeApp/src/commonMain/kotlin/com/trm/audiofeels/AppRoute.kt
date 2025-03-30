package com.trm.audiofeels

import com.trm.audiofeels.domain.model.CarryOnPlaylist
import com.trm.audiofeels.domain.model.Mood
import com.trm.audiofeels.domain.model.Playlist
import kotlinx.serialization.Serializable

sealed interface AppRoute {
  @Serializable data object DiscoverPage : AppRoute

  @Serializable data object SearchPage : AppRoute

  @Serializable data class MoodPage(val mood: Mood) : AppRoute

  @Serializable
  data class PlaylistsPage(val title: String, val playlists: List<Playlist>) : AppRoute

  @Serializable data class CarryOnPlaylistsPage(val playlists: List<CarryOnPlaylist>) : AppRoute
}
