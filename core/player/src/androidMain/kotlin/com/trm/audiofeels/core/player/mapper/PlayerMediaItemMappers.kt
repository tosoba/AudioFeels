package com.trm.audiofeels.core.player.mapper

import android.net.Uri
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import com.trm.audiofeels.core.player.util.buildStreamUrl
import com.trm.audiofeels.domain.model.Track

internal fun Track.toMediaItem(host: String): MediaItem =
  MediaItem.Builder()
    .setMediaId(id)
    .setRequestMetadata(
      RequestMetadata.Builder().setMediaUri(buildStreamUrl(host = host).toUri()).build()
    )
    .setMediaMetadata(
      MediaMetadata.Builder()
        .setArtworkUri(artworkUrl?.let(Uri::parse))
        .setTitle(title)
        .setIsBrowsable(false)
        .setIsPlayable(true)
        .setExtras(
          bundleOf(
            ARTWORK_URL_EXTRA to artworkUrl,
            DESCRIPTION_EXTRA to description,
            DURATION_EXTRA to duration,
            GENRE_EXTRA to genre,
            MOOD_EXTRA to mood,
            PLAY_COUNT_EXTRA to playCount,
            TAGS_EXTRA to tags,
          )
        )
        .build()
    )
    .build()

internal fun MediaItem.toTrack(): Track {
  val extras = requireNotNull(mediaMetadata.extras)
  return Track(
    artworkUrl = extras.getString(ARTWORK_URL_EXTRA),
    description = extras.getString(DESCRIPTION_EXTRA),
    duration = extras.getInt(DURATION_EXTRA),
    genre = extras.getString(GENRE_EXTRA),
    id = mediaId,
    mood = extras.getString(MOOD_EXTRA),
    playCount = extras.getInt(PLAY_COUNT_EXTRA),
    tags = extras.getString(TAGS_EXTRA),
    title = mediaMetadata.title?.toString().orEmpty(),
  )
}

private const val ARTWORK_URL_EXTRA = "ARTWORK_URL_EXTRA"
private const val DESCRIPTION_EXTRA = "DESCRIPTION_EXTRA"
private const val DURATION_EXTRA = "DURATION_EXTRA"
private const val GENRE_EXTRA = "GENRE_EXTRA"
private const val MOOD_EXTRA = "MOOD_EXTRA"
private const val PLAY_COUNT_EXTRA = "PLAY_COUNT_EXTRA"
private const val TAGS_EXTRA = "TAGS_EXTRA"
