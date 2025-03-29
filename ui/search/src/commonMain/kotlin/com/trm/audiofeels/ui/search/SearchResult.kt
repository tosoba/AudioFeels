package com.trm.audiofeels.ui.search

import com.trm.audiofeels.domain.model.Playlist

data class SearchResult(val query: String, val playlists: List<Playlist>)
