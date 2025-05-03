package com.trm.audiofeels.core.player.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.trm.audiofeels.core.player.PlayerService

interface PlayerServiceComponentFactory {
  fun create(@OptIn(UnstableApi::class) service: PlayerService): BasePlayerServiceComponent
}
