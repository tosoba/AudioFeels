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
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.ui.search" }
