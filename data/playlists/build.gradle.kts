plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.api.audius)
        implementation(projects.core.network)
        implementation(projects.domain)

        implementation(libs.ktor.client.core)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlininject.runtime)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.data.playlists" }
