package com.trm.audiofeels.di

import android.app.Application
import com.trm.audiofeels.api.audius.di.AudiusApiComponent
import com.trm.audiofeels.api.hosts.di.HostsApiComponent
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.cache.di.CacheCoreComponent
import com.trm.audiofeels.core.player.di.PlayerComponent
import com.trm.audiofeels.core.preferences.di.PreferencesCoreComponent
import com.trm.audiofeels.data.hosts.di.HostsDataComponent
import com.trm.audiofeels.data.playlists.di.PlaylistsDataComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ApplicationScope
@Component
abstract class AndroidApplicationComponent(@get:Provides val application: Application) :
  AudiusApiComponent,
  HostsApiComponent,
  CacheCoreComponent,
  PreferencesCoreComponent,
  HostsDataComponent,
  PlayerComponent,
  PlaylistsDataComponent {
  companion object
}
