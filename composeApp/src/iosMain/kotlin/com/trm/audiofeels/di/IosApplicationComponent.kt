package com.trm.audiofeels.di

import com.trm.audiofeels.api.audius.di.AudiusApiComponent
import com.trm.audiofeels.api.hosts.di.HostsApiComponent
import com.trm.audiofeels.core.base.di.ApplicationScope
import com.trm.audiofeels.core.cache.di.CacheCoreComponent
import com.trm.audiofeels.core.preferences.di.PreferencesCoreComponent
import com.trm.audiofeels.data.hosts.di.HostsDataComponent
import com.trm.audiofeels.data.playlists.di.PlaylistsDataComponent
import me.tatarka.inject.annotations.Component

@ApplicationScope
@Component
abstract class IosApplicationComponent :
  AudiusApiComponent,
  HostsApiComponent,
  CacheCoreComponent,
  PreferencesCoreComponent,
  HostsDataComponent,
  PlaylistsDataComponent {
  companion object
}
