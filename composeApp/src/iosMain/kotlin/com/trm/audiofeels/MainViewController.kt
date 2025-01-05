package com.trm.audiofeels

import androidx.compose.ui.window.ComposeUIViewController
import com.trm.audiofeels.core.base.util.initNapierDebug
import com.trm.audiofeels.di.ApplicationComponent
import platform.UIKit.UIViewController

fun MainViewController(applicationComponent: ApplicationComponent): UIViewController =
  ComposeUIViewController(configure = { initNapierDebug() }) { AppContent(applicationComponent) }
