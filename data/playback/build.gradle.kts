plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.preferences)
        implementation(projects.domain)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlininject.runtime)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.data.playback" }
