package com.trm.audiofeels.core.player.di

import androidx.media3.common.util.UnstableApi
import com.trm.audiofeels.core.player.PlayerNotificationProvider
import com.trm.audiofeels.core.player.PlayerSessionCallback

interface PlayerServiceComponent {
  @get:UnstableApi val playerNotificationProvider: PlayerNotificationProvider

  val playerSessionCallback: PlayerSessionCallback
}
