plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonTest {
      dependencies {
        implementation(projects.api.audius)
        implementation(projects.api.hosts)
        implementation(projects.core.network)
        implementation(projects.data.hosts)
        implementation(projects.data.playlists)
        implementation(projects.domain)

        implementation(libs.androidx.datastore)
        implementation(libs.androidx.datastore.preferences)

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
