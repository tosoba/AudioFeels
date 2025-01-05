package com.trm.audiofeels.core.player.di

import com.trm.audiofeels.core.player.PlayerConnection
import com.trm.audiofeels.core.player.PlayerPlatformConnection
import me.tatarka.inject.annotations.Provides

interface PlayerComponent {
  @Provides
  fun bindPlatformConnection(connection: PlayerPlatformConnection): PlayerConnection = connection
}
