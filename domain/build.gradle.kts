plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)

        implementation(libs.kotlinx.coroutines.core)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.domain" }
