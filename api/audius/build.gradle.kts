plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.api.audius"
    androidResources { enable = true }
  }

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

        implementation(libs.kotlinx.serialization.json)

        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.serialization.kotlinx.json)
      }
    }

    iosMain { dependencies { implementation(libs.ktor.client.darwin) } }
  }
}
