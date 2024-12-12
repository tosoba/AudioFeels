package com.trm.audiofeels.core.player

import android.net.Uri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.trm.audiofeels.core.network.buildTrackStreamUrl
import com.trm.audiofeels.core.player.model.PlaybackState
import com.trm.audiofeels.domain.model.Track

internal fun Track.toMediaItem(host: String): MediaItem =
  MediaItem.Builder()
    .setMediaId(id)
    .setRequestMetadata(
      RequestMetadata.Builder()
        .setMediaUri(Uri.parse(buildTrackStreamUrl(id = id, host = host)))
        .build()
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

internal fun MediaItem.toTrack(): Track =
  Track(
    artworkUrl = mediaMetadata.extras?.getString(ARTWORK_URL_EXTRA),
    description = mediaMetadata.extras?.getString(DESCRIPTION_EXTRA),
    duration = mediaMetadata.extras?.getInt(DURATION_EXTRA),
    genre = mediaMetadata.extras?.getString(GENRE_EXTRA),
    id = mediaId,
    mood = mediaMetadata.extras?.getString(MOOD_EXTRA),
    playCount = mediaMetadata.extras?.getInt(PLAY_COUNT_EXTRA),
    tags = mediaMetadata.extras?.getString(TAGS_EXTRA),
    title = mediaMetadata.title?.toString().orEmpty(),
  )

private const val ARTWORK_URL_EXTRA = "ARTWORK_URL_EXTRA"
private const val DESCRIPTION_EXTRA = "DESCRIPTION_EXTRA"
private const val DURATION_EXTRA = "DURATION_EXTRA"
private const val GENRE_EXTRA = "GENRE_EXTRA"
private const val MOOD_EXTRA = "MOOD_EXTRA"
private const val PLAY_COUNT_EXTRA = "PLAY_COUNT_EXTRA"
private const val TAGS_EXTRA = "TAGS_EXTRA"

internal fun enumPlaybackStateOf(@Player.State state: Int): PlaybackState =
  when (state) {
    Player.STATE_IDLE -> PlaybackState.IDLE
    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
    Player.STATE_READY -> PlaybackState.READY
    Player.STATE_ENDED -> PlaybackState.ENDED
    else -> throw IllegalArgumentException()
  }
