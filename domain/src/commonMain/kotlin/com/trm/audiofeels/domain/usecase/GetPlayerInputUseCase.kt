package com.trm.audiofeels.domain.usecase

import com.trm.audiofeels.domain.model.PlayerInput
import com.trm.audiofeels.domain.repository.HostsRepository
import com.trm.audiofeels.domain.repository.PlaybackRepository
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject

@Inject
class GetPlayerInputUseCase(
  private val playlistsRepository: PlaylistsRepository,
  private val hostsRepository: HostsRepository,
  private val playbackRepository: PlaybackRepository,
) {
  suspend operator fun invoke(playlistId: String): PlayerInput = coroutineScope {
    val tracks = async { playlistsRepository.getPlaylistTracks(playlistId) }
    val host = async { hostsRepository.retrieveHost() }
    val start = async { playbackRepository.getPlaybackStart() }
    PlayerInput(tracks = tracks.await(), host = host.await(), start = start.await())
  }
}
