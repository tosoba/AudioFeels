package com.trm.audiofeels.core.player.di

import com.trm.audiofeels.core.player.AudioPlayerConnection
import com.trm.audiofeels.domain.player.PlayerConnection
import me.tatarka.inject.annotations.Provides

interface PlayerComponent {
  @Provides
  fun bindPlatformConnection(connection: AudioPlayerConnection): PlayerConnection = connection
}
