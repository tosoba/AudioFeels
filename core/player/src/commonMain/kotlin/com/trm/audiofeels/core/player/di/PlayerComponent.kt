package com.trm.audiofeels.core.player.di

import com.trm.audiofeels.core.player.PlayerDeviceConnection
import com.trm.audiofeels.domain.player.PlayerConnection
import me.tatarka.inject.annotations.Provides

interface PlayerComponent {
  @Provides
  fun bindPlayerConnection(connection: PlayerDeviceConnection): PlayerConnection = connection
}
