package com.trm.audiofeels

import androidx.compose.ui.window.ComposeUIViewController
import com.trm.audiofeels.di.ApplicationComponent

fun MainViewController(applicationComponent: ApplicationComponent) = ComposeUIViewController {
  AppContent(applicationComponent)
}
