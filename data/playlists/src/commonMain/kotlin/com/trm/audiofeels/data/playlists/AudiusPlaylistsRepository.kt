package com.trm.audiofeels.data.playlists

import com.trm.audiofeels.api.audius.AudiusEndpoints
import com.trm.audiofeels.api.audius.model.PlaylistsResponseItem
import com.trm.audiofeels.api.audius.model.TrackResponseItem
import com.trm.audiofeels.data.playlists.util.isValid
import com.trm.audiofeels.data.playlists.util.toPlaylist
import com.trm.audiofeels.data.playlists.util.toTrack
import com.trm.audiofeels.domain.model.Playlist
import com.trm.audiofeels.domain.model.Track
import com.trm.audiofeels.domain.repository.PlaylistsRepository
import me.tatarka.inject.annotations.Inject

@Inject
class AudiusPlaylistsRepository(private val audiusEndpoints: AudiusEndpoints) :
  PlaylistsRepository {
  override suspend fun getPlaylists(mood: String?): List<Playlist> =
    audiusEndpoints
      .getPlaylists(mood)
      .items
      ?.filter(PlaylistsResponseItem::isValid)
      ?.map(PlaylistsResponseItem::toPlaylist)
      .orEmpty()

  override suspend fun getPlaylistTracks(playlistId: String): List<Track> =
    audiusEndpoints
      .getPlaylistById(playlistId)
      .items
      ?.firstOrNull()
      ?.tracks
      ?.filter(TrackResponseItem::isValid)
      ?.map(TrackResponseItem::toTrack)
      .orEmpty()
}
