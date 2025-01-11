package com.trm.audiofeels.di

import com.trm.audiofeels.api.audius.di.AudiusApiComponent
import com.trm.audiofeels.api.hosts.di.HostsApiComponent
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.base.util.AppCoroutineDispatchers
import com.trm.audiofeels.core.base.util.AppCoroutineScope
import com.trm.audiofeels.core.network.di.NetworkCoreComponent
import com.trm.audiofeels.core.player.di.PlayerComponent
import com.trm.audiofeels.core.preferences.di.PreferencesCoreComponent
import com.trm.audiofeels.data.hosts.di.HostsDataComponent
import com.trm.audiofeels.data.playback.di.PlaybackDataComponent
import com.trm.audiofeels.data.playlists.di.PlaylistsDataComponent
import com.trm.audiofeels.ui.discover.di.DiscoverUiComponent
import com.trm.audiofeels.ui.player.di.PlayerUiComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import me.tatarka.inject.annotations.Provides

interface ApplicationComponent :
  AudiusApiComponent,
  HostsApiComponent,
  NetworkCoreComponent,
  PreferencesCoreComponent,
  HostsDataComponent,
  PlaybackDataComponent,
  PlaylistsDataComponent,
  PlayerComponent,
  DiscoverUiComponent,
  PlayerUiComponent {
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
  fun provideApplicationCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
    CoroutineScope(dispatchers.main + SupervisorJob())
}
