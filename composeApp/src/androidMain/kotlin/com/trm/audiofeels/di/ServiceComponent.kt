package com.trm.audiofeels.di

import android.app.Service
import android.content.Context
import com.trm.audiofeels.core.base.di.ServiceContext
import com.trm.audiofeels.core.base.di.ServiceScope
import com.trm.audiofeels.core.player.di.PlayerServiceComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ServiceScope
@Component
abstract class ServiceComponent(
  @get:Provides val service: Service,
  @Component val applicationComponent: AndroidApplicationComponent,
) : PlayerServiceComponent {
  @Provides fun bindServiceContext(): @ServiceContext Context = service

  companion object
}
