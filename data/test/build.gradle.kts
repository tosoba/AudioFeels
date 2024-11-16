plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.mokkery)
}

kotlin {
  sourceSets {
    commonTest {
      dependencies {
        implementation(projects.api.audius)
        implementation(projects.api.hosts)
        implementation(projects.core.base)
        implementation(projects.core.network)
        implementation(projects.core.preferences)
        implementation(projects.data.hosts)
        implementation(projects.data.playlists)
        implementation(projects.domain)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.kotlinx.serialization.json)

        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.mock)
        implementation(libs.ktor.serialization.kotlinx.json)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.data.test" }
