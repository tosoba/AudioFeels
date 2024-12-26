package com.trm.audiofeels.core.player

import androidx.media3.common.util.UnstableApi
import com.trm.audiofeels.core.base.LifecycleMediaLibraryService
import com.trm.audiofeels.core.player.di.BasePlayerServiceComponent
import com.trm.audiofeels.core.player.di.PlayerServiceComponentFactory

@UnstableApi
class PlayerService : LifecycleMediaLibraryService() {
  private val component: BasePlayerServiceComponent by
    lazy(LazyThreadSafetyMode.NONE) { (application as PlayerServiceComponentFactory).create(this) }

  override val mediaLibrarySession: MediaLibrarySession by
    lazy(LazyThreadSafetyMode.NONE) { component.mediaLibrarySession }

  override fun onCreate() {
    super.onCreate()
    setMediaNotificationProvider(component.playerNotificationProvider)
  }
}
