package com.trm.audiofeels.data.playlists.di

import com.trm.audiofeels.data.playlists.AudiusPlaylistsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import me.tatarka.inject.annotations.Provides

interface PlaylistsDataComponent {
  val playlistsRepository: PlaylistsRepository

  @Provides
  fun bindPlaylistsRepository(repository: AudiusPlaylistsRepository): PlaylistsRepository =
    repository
}
