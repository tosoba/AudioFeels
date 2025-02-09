package com.trm.audiofeels.domain.usecase

import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject

@Inject
class GetPlayerInputUseCase(
  private val playlistsRepository: PlaylistsRepository,
  private val hostsRepository: HostsRepository,
) {
  suspend operator fun invoke(playlistId: String): PlayerInput = coroutineScope {
    val tracks = async { playlistsRepository.getPlaylistTracks(playlistId) }
    val host = async { hostsRepository.retrieveHost() }
    PlayerInput(tracks = tracks.await(), host = "https://${host.await()}")
  }
}
