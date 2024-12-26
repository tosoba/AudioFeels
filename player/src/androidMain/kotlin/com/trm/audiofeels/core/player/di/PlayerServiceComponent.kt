package com.trm.audiofeels.core.player.di

import android.app.Service
import androidx.media3.common.util.UnstableApi
import com.trm.audiofeels.core.player.PlayerNotificationProvider
import com.trm.audiofeels.core.player.PlayerSessionCallback

interface PlayerServiceComponent {
  val service: Service

  @get:UnstableApi val playerNotificationProvider: PlayerNotificationProvider

  val playerSessionCallback: PlayerSessionCallback
}
