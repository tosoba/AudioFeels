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
        implementation(projects.core.ui.resources)
        implementation(projects.core.network)

        api(libs.compose.components.resources)
        api(libs.compose.components.uiToolingPreview)
        api(libs.compose.foundation)
        api(libs.compose.material)
        api(libs.compose.material3)
        api(libs.compose.materialIconsExtended)
        api(libs.compose.runtime)
        api(libs.compose.ui)

        implementation(libs.coil.core)
        implementation(libs.coil.compose)
        implementation(libs.haze)

        implementation(libs.material3.adaptive)
        implementation(libs.material3.adaptive.layout)
        implementation(libs.material3.adaptive.navigation)
        implementation(libs.material3.navigation.suite)
        implementation(libs.material3.windowSizeClass)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.core.ui.compose" }
