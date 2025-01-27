plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.preferences)
        implementation(projects.domain)

        implementation(libs.kotlinx.serialization.json)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.data.playback" }
