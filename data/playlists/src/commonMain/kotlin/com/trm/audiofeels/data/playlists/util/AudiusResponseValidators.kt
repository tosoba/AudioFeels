package com.trm.audiofeels.data.playlists.util

import com.trm.audiofeels.api.audius.model.PlaylistsResponseItem
import com.trm.audiofeels.api.audius.model.TrackResponseItem

internal val TrackResponseItem.isValid: Boolean
  get() =
    id != null &&
      title != null &&
      duration != null &&
      isAvailable == true &&
      isPremium != true &&
      isDelete != true &&
      isStreamable == true

internal val PlaylistsResponseItem.isValid: Boolean
  get() = id != null && playlistName != null && (trackCount ?: 0) > 0
