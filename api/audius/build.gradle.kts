plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.cache)
        implementation(projects.core.network)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlininject.runtime)

        implementation(libs.ktor.client.core)
        implementation(libs.ktor.serialization.kotlinx.json)
      }
    }

    commonTest { dependencies { implementation(libs.kotlin.test) } }
  }
}

android { namespace = "com.trm.audiofeels.api.audius" }
