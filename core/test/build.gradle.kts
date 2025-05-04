plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.parcelize)
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.androidx.test.core)
        implementation(libs.androidx.test.junit)
        implementation(libs.robolectric)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.preferences)
        implementation(projects.domain)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.core.test" }
