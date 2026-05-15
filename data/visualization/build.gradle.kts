plugins { id("com.trm.audiofeels.kotlin.multiplatform") }

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.data.visualization"
    androidResources { enable = true }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.preferences)
        implementation(projects.domain)
      }
    }
  }
}
