package com.trm.audiofeels.di

import com.trm.audiofeels.api.audius.di.AudiusApiComponent
import com.trm.audiofeels.api.hosts.di.HostsApiComponent
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.AppCoroutineDispatchers
import com.trm.audiofeels.core.base.util.ApplicationCoroutineScope
import com.trm.audiofeels.core.cache.di.CacheCoreComponent
import com.trm.audiofeels.core.player.di.PlayerComponent
import com.trm.audiofeels.core.preferences.di.PreferencesCoreComponent
import com.trm.audiofeels.data.hosts.di.HostsDataComponent
import com.trm.audiofeels.data.playlists.di.PlaylistsDataComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Provides

interface ApplicationComponent :
  AudiusApiComponent,
  HostsApiComponent,
  CacheCoreComponent,
  PreferencesCoreComponent,
  HostsDataComponent,
  PlayerComponent,
  PlaylistsDataComponent {
  @ApplicationScope
  @Provides
  fun provideCoroutineDispatchers(): AppCoroutineDispatchers =
    AppCoroutineDispatchers(
      io = Dispatchers.IO,
      computation = Dispatchers.Default,
      main = Dispatchers.Main,
    )

  @ApplicationScope
  @Provides
  fun provideApplicationCoroutineScope(
    dispatchers: AppCoroutineDispatchers
  ): ApplicationCoroutineScope = CoroutineScope(dispatchers.main + SupervisorJob())
}
