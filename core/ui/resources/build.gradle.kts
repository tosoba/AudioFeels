plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
}

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.core.ui.resources"
    androidResources { enable = true }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)

        api(libs.compose.ui)
        api(libs.compose.components.resources)
        implementation(libs.compose.runtime)
      }
    }
  }
}

compose.resources {
  publicResClass = true
  packageOfResClass = "com.trm.audiofeels.core.ui.resources"
}
