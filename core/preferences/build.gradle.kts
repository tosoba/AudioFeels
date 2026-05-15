plugins { id("com.trm.audiofeels.kotlin.multiplatform") }

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.core.preferences"
    androidResources { enable = true }
  }

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
