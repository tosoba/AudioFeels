package com.trm.audiofeels.core.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.trm.audiofeels.core.base.di.AndroidComponentProvider
import com.trm.audiofeels.core.player.di.PlayerPlatformComponent

@UnstableApi
class PlayerService : MediaLibraryService() {
  private var mediaLibrarySession: MediaLibrarySession? = null
  private val playerNotificationProvider: PlayerNotificationProvider by
    lazy(LazyThreadSafetyMode.NONE) {
      (application as AndroidComponentProvider)
        .provideComponent(PlayerPlatformComponent::class.java)
        .playerNotificationProvider
    }

  override fun onCreate() {
    super.onCreate()
    mediaLibrarySession = buildMediaLibrarySession(buildExoPlayer(buildAudioAttributes()))
    setMediaNotificationProvider(playerNotificationProvider)
  }

  override fun onDestroy() {
    super.onDestroy()
    releaseSession()
    playerNotificationProvider.cancelCoroutineScope()
  }

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
    mediaLibrarySession

  private fun buildMediaLibrarySession(player: ExoPlayer) =
    MediaLibrarySession.Builder(this, player, PlayerSessionCallback).build()

  private fun buildAudioAttributes(): AudioAttributes =
    AudioAttributes.Builder()
      .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
      .setUsage(C.USAGE_MEDIA)
      .build()

  private fun buildExoPlayer(audioAttributes: AudioAttributes): ExoPlayer =
    ExoPlayer.Builder(this)
      .setAudioAttributes(audioAttributes, true)
      .setHandleAudioBecomingNoisy(true)
      .build()

  private fun releaseSession() {
    mediaLibrarySession?.run {
      player.release()
      release()
      mediaLibrarySession = null
    }
  }
}
