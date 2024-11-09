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
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.coroutines.core)
      }
    }

    androidMain { dependencies {} }

    iosMain { dependencies {} }
  }
}

android { namespace = "com.trm.audiofeels.core.base" }
