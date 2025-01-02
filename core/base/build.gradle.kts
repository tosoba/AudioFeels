plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.androidx.lifecycle.service)

        implementation(libs.androidx.media3.exoplayer)
        implementation(libs.androidx.media3.session)
        implementation(libs.androidx.media3.ui)
      }
    }

    commonMain {
      dependencies {
        api(libs.kermit)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlininject.runtime)
        implementation(libs.kotlinx.coroutines.core)

        implementation(libs.okio)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.turbine)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.core.base" }
