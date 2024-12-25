plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlininject.runtime)
        implementation(libs.kotlinx.coroutines.core)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.shared" }
