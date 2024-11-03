plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlininject.runtime)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.okio)
      }
    }

    commonTest { dependencies { implementation(libs.kotlin.test) } }

    androidMain { dependencies {} }

    iosMain { dependencies {} }
  }
}

android { namespace = "com.trm.audiofeels.core.network" }
