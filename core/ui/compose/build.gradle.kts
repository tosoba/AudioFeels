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
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.window)
      }
    }

    commonMain {
      dependencies {
        implementation(compose.foundation)
        implementation(compose.material)
        api(compose.materialIconsExtended)
        api(compose.material3)

        implementation(libs.material3.adaptive)
        implementation(libs.material3.adaptive.layout)
        implementation(libs.material3.adaptive.navigation)
        implementation(compose.material3AdaptiveNavigationSuite)

        implementation(libs.material3.windowSizeClass)
      }
    }
  }
}

android {
  namespace = "com.trm.audiofeels.core.ui.compose"
}
