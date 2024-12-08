plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.domain)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlininject.runtime)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.core.player" }
