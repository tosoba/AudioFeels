package com.trm.audiofeels.core.player.di

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import com.trm.audiofeels.core.base.di.BaseServiceComponent
import com.trm.audiofeels.core.base.di.ServiceLifecycleScope
import com.trm.audiofeels.core.player.PlayerNotificationProvider
import com.trm.audiofeels.core.player.PlayerService
import com.trm.audiofeels.core.player.PlayerSessionCallback
import me.tatarka.inject.annotations.Provides

interface BasePlayerServiceComponent : BaseServiceComponent {
  override val service: PlayerService

  @get:UnstableApi val playerNotificationProvider: PlayerNotificationProvider

  val mediaLibrarySession: MediaLibrarySession

  @Provides
  fun bindServiceLifecycleScope(): @ServiceLifecycleScope LifecycleCoroutineScope =
    service.lifecycleScope

  @Provides
  fun mediaLibrarySession(playerSessionCallback: PlayerSessionCallback): MediaLibrarySession =
    MediaLibrarySession.Builder(
        service,
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
        service.lifecycle.addObserver(
          object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
              it.release()
            }
          }
        )
      }
}
