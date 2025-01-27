plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.parcelize)
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
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.lifecycle.viewmodel.compose)

        api(libs.kotlin.stdlib)
        api(libs.kotlininject.runtime)
        api(libs.kotlinx.coroutines.core)
        api(libs.kotlinx.datetime)
        api(libs.kotlinx.serialization.json)

        api(libs.napier)
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
