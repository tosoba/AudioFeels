plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
  alias(libs.plugins.mokkery)
}

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.ui.player"
    androidResources { enable = true }
  }

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
