plugins {
  id("com.trm.audiofeels.android.application")
  id("com.trm.audiofeels.compose")
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.trm.audiofeels"
  defaultConfig {
    applicationId = "com.trm.audiofeels"
    versionCode = 1
    versionName = "1.0"
  }
  buildFeatures { buildConfig = true }
}

ksp { arg("me.tatarka.inject.generateCompanionExtensions", "true") }

dependencies {
  implementation(projects.composeApp)

  implementation(libs.androidx.activity.compose)

  implementation(libs.androidx.media3.exoplayer)
  implementation(libs.androidx.media3.session)
  implementation(libs.androidx.media3.ui)

  implementation(libs.kotlininject.runtime)
  ksp(libs.kotlininject.compiler)
}
