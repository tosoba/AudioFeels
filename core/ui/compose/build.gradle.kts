plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.window)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.network)

        api(compose.components.resources)
        api(compose.components.uiToolingPreview)
        api(compose.foundation)
        api(compose.material)
        api(compose.material3)
        api(compose.materialIconsExtended)
        api(compose.runtime)
        api(compose.ui)

        implementation(libs.coil.core)

        implementation(compose.material3AdaptiveNavigationSuite)
        implementation(libs.material3.adaptive)
        implementation(libs.material3.adaptive.layout)
        implementation(libs.material3.adaptive.navigation)
        implementation(libs.material3.windowSizeClass)
      }
    }
  }
}

android {
  namespace = "com.trm.audiofeels.core.ui.compose"
}
