package com.trm.audiofeels.di

import android.app.Service
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.trm.audiofeels.core.base.di.ServiceContext
import com.trm.audiofeels.core.base.di.ServiceLifecycleScope
import com.trm.audiofeels.core.base.di.ServiceScope
import com.trm.audiofeels.core.player.di.PlayerServiceComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ServiceScope
@Component
abstract class ServiceComponent(
  @get:Provides override val service: Service,
  @Component val applicationComponent: AndroidApplicationComponent,
) : PlayerServiceComponent {
  @Provides fun bindServiceContext(): @ServiceContext Context = service

  @Provides
  fun bindLifecycleScope(): @ServiceLifecycleScope LifecycleCoroutineScope =
    (service as LifecycleOwner).lifecycleScope

  companion object
}
