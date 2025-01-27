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

        api(compose.ui)
        api(compose.components.resources)
        implementation(compose.runtime)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.core.ui.resources" }

compose.resources {
  publicResClass = true
  packageOfResClass = "com.trm.audiofeels.core.ui.resources"
}
