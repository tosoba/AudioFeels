plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.api.hosts)
        implementation(projects.core.base)
        implementation(projects.core.network)
        implementation(projects.core.preferences)
        implementation(projects.domain)
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.data.hosts" }
