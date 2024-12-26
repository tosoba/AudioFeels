package com.trm.audiofeels.core.player.di

import android.app.Service
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import com.trm.audiofeels.core.player.PlayerNotificationProvider
import com.trm.audiofeels.core.player.PlayerSessionCallback
import me.tatarka.inject.annotations.Provides

interface PlayerServiceComponent {
  val service: Service

  @get:UnstableApi val playerNotificationProvider: PlayerNotificationProvider

  val playerSessionCallback: PlayerSessionCallback

  val mediaLibrarySession: MediaLibrarySession

  @Provides
  fun mediaLibrarySession(): MediaLibrarySession =
    MediaLibrarySession.Builder(
        service as MediaLibraryService,
        ExoPlayer.Builder(service)
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
              .setUsage(C.USAGE_MEDIA)
              .build(),
            true,
          )
          .setHandleAudioBecomingNoisy(true)
          .build(),
        playerSessionCallback,
      )
      .build()
      .also {
        (service as LifecycleOwner)
          .lifecycle
          .addObserver(
            object : DefaultLifecycleObserver {
              override fun onDestroy(owner: LifecycleOwner) {
                it.release()
              }
            }
          )
      }
}
