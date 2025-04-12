plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
  alias(libs.plugins.mokkery)
}

kotlin {
  sourceSets {
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
        implementation(libs.haze)

        implementation(libs.material3.adaptive)
      }
    }

    commonTest {
      dependencies {
        implementation(projects.data.test)

        implementation(libs.kotlin.test)
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.turbine)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.ui.search" }
