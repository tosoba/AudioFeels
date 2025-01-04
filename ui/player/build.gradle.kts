plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.network)
        implementation(projects.core.ui.compose)
        implementation(projects.core.ui.resources)
        implementation(projects.domain)
        implementation(projects.player)

        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.lifecycle.viewmodel.compose)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlininject.runtime)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.ui.player" }
