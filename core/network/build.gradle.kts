plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    androidMain { dependencies { implementation(libs.androidx.core.ktx) } }

    commonMain {
      dependencies {
        implementation(projects.core.base)

        api(libs.coil.core)
        api(libs.coil.network)

        implementation(libs.kachetor)

        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.kotlinx.json)

        implementation(libs.okio)
      }
    }

    commonTest { dependencies { implementation(libs.kotlin.test) } }
  }
}

android { namespace = "com.trm.audiofeels.core.network" }
