package com.trm.audiofeels.core.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.trm.audiofeels.core.base.di.ServiceComponentFactory
import com.trm.audiofeels.core.player.di.PlayerServiceComponent

@UnstableApi
class PlayerService : MediaLibraryService() {
  private var mediaLibrarySession: MediaLibrarySession? = null

  private val component: PlayerServiceComponent by
    lazy(LazyThreadSafetyMode.NONE) {
      (application as ServiceComponentFactory).create(this, PlayerServiceComponent::class)
    }

  override fun onCreate() {
    super.onCreate()
    mediaLibrarySession = buildMediaLibrarySession(buildExoPlayer(buildAudioAttributes()))
    setMediaNotificationProvider(component.playerNotificationProvider)
  }

  override fun onDestroy() {
    super.onDestroy()
    releaseSession()
    component.playerNotificationProvider.cancelCoroutineScope()
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
