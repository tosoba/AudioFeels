plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.database)
        implementation(projects.domain)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.data.playback" }
