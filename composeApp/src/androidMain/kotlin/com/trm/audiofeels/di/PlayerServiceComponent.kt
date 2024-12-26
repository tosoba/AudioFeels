package com.trm.audiofeels.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.trm.audiofeels.core.base.di.ServiceScope
import com.trm.audiofeels.core.player.PlayerService
import com.trm.audiofeels.core.player.di.BasePlayerServiceComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@OptIn(UnstableApi::class)
@ServiceScope
@Component
abstract class PlayerServiceComponent(
  @get:Provides override val service: PlayerService,
  @Component val parent: AndroidApplicationComponent,
) : BasePlayerServiceComponent {
  companion object
}
