plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.ktor.client.android)
        implementation(libs.ktor.client.okhttp)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.network)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlininject.runtime)

        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.serialization.kotlinx.json)
      }
    }

    iosMain {
      dependencies {
        implementation(libs.ktor.client.darwin)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.api.audius" }
