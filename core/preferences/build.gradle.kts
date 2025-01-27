plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)

        api(libs.androidx.datastore)
        api(libs.androidx.datastore.preferences)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.core.preferences" }
