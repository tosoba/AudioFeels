plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.mokkery)
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.androidx.test.core)
        implementation(libs.robolectric)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.api.audius)
        implementation(projects.core.base)
        implementation(projects.core.preferences)
        implementation(projects.core.test)
        implementation(projects.data.database)
        implementation(projects.data.playback)
        implementation(projects.data.visualization)
        implementation(projects.domain)

        implementation(libs.room.runtime)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.api.audius)
        implementation(projects.api.hosts)
        implementation(projects.core.base)
        implementation(projects.core.network)
        implementation(projects.core.preferences)
        implementation(projects.core.test)
        implementation(projects.data.database)
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
