plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.ui.compose)
        implementation(projects.core.ui.resources)

        implementation(libs.material3.adaptive)
        implementation(libs.material3.adaptive.layout)
        implementation(libs.material3.adaptive.navigation)
        implementation(compose.material3AdaptiveNavigationSuite)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlininject.runtime)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.ui.discover" }
