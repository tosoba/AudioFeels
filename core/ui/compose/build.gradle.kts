plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.materialIconsExtended)
        api(compose.material3)
      }
    }
  }
}

android {
  namespace = "com.trm.audiofeels.core.ui.compose"
}
