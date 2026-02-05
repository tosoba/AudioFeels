plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
}

kotlin {
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

android { namespace = "com.trm.audiofeels.core.ui.resources" }

compose.resources {
  publicResClass = true
  packageOfResClass = "com.trm.audiofeels.core.ui.resources"
}
