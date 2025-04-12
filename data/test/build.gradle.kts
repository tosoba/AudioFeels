plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.mokkery)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.preferences)
        implementation(projects.domain)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.api.audius)
        implementation(projects.api.hosts)
        implementation(projects.core.base)
        implementation(projects.core.database)
        implementation(projects.core.network)
        implementation(projects.core.preferences)
        implementation(projects.data.hosts)
        implementation(projects.data.playlists)
        implementation(projects.data.suggestions)
        implementation(projects.domain)

        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.coroutines.test)

        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.client.mock)
        implementation(libs.ktor.serialization.kotlinx.json)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.data.test" }
