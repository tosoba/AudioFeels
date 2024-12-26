package com.trm.audiofeels.core.base

import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

abstract class LifecycleMediaLibraryService : MediaLibraryService(), LifecycleOwner {
  @Suppress("LeakingThis") private val dispatcher = ServiceLifecycleDispatcher(this)

  override val lifecycle: Lifecycle
    get() = dispatcher.lifecycle

  protected abstract val mediaLibrarySession: MediaLibrarySession?

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
    mediaLibrarySession

  @CallSuper
  override fun onCreate() {
    dispatcher.onServicePreSuperOnCreate()
    super.onCreate()
  }

  @CallSuper
  override fun onBind(intent: Intent?): IBinder? {
    dispatcher.onServicePreSuperOnBind()
    return super.onBind(intent)
  }

  @Deprecated("Use onStartCommand")
  final override fun onStart(intent: Intent?, startId: Int) {
    dispatcher.onServicePreSuperOnStart()
    @Suppress("DEPRECATION") super.onStart(intent, startId)
  }

  @CallSuper
  final override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
    super.onStartCommand(intent, flags, startId)

  @CallSuper
  override fun onDestroy() {
    dispatcher.onServicePreSuperOnDestroy()
    super.onDestroy()
  }
}
