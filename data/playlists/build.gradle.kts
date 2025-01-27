plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.api.audius)
        implementation(projects.core.base)
        implementation(projects.core.database)
        implementation(projects.core.network)
        implementation(projects.domain)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.data.playlists" }
