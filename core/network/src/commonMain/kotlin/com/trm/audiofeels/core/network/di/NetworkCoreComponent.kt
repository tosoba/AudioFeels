package com.trm.audiofeels.core.network.di

import com.trm.audiofeels.core.network.host.HostValidator
import me.tatarka.inject.annotations.Provides

interface NetworkCoreComponent : NetworkPlatformComponent {
  @Provides fun hostValidator(): HostValidator = HostValidator()
}
