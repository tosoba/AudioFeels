plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.parcelize)
}

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.core.test"
    androidResources { enable = true }
  }

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
