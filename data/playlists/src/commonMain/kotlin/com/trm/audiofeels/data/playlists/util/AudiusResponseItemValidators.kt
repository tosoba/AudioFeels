package com.trm.audiofeels.data.playlists.util

import com.trm.audiofeels.api.audius.model.PlaylistsResponseItem
import com.trm.audiofeels.api.audius.model.TrackResponseItem

val TrackResponseItem.isValid: Boolean
  get() =
    id != null &&
      title != null &&
      isAvailable == true &&
      isPremium == false &&
      isDelete == false &&
      isStreamable == true

val PlaylistsResponseItem.isValid: Boolean
  get() = id != null && playlistName != null && (trackCount ?: 0) > 0
