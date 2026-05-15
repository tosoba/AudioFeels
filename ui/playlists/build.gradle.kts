plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
}

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.ui.playlists"
    androidResources { enable = true }
  }

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
  }
}
