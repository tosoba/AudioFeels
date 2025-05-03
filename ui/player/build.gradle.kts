plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
  alias(libs.plugins.mokkery)
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.accompanist.permissions)
        implementation(libs.androidx.activity.compose)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.network)
        implementation(projects.core.ui.compose)
        implementation(projects.core.ui.resources)
        implementation(projects.domain)

        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.lifecycle.viewmodel.compose)

        implementation(libs.coil.compose)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.core.test)
        implementation(projects.data.test)

        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.turbine)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.ui.player" }
