plugins { id("com.trm.audiofeels.kotlin.multiplatform") }

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.data.suggestions"
    androidResources { enable = true }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.data.database)
        implementation(projects.domain)
      }
    }
  }
}
