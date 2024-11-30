plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.accompanist.adaptive)
        implementation(libs.androidx.window)
      }
    }

    commonMain {
      dependencies {
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.materialIconsExtended)
        api(compose.material3)
        implementation(compose.material3AdaptiveNavigationSuite)

        implementation(libs.material3.windowSizeClass)
      }
    }
  }
}

android {
  namespace = "com.trm.audiofeels.core.ui.compose"
}
