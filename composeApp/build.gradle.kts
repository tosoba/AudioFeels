import com.trm.audiofeels.gradle.addKspDependencyForAllTargets
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.compose")
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  android {
    namespace = "com.trm.audiofeels.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
  }

  targets.withType<KotlinNativeTarget>().forEach { nativeTarget ->
    nativeTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      api(projects.api.audius)
      api(projects.api.hosts)
      api(projects.core.base)
      api(projects.core.network)
      api(projects.core.player)
      api(projects.core.preferences)
      api(projects.core.ui.compose)
      api(projects.core.ui.resources)
      api(projects.data.database)
      api(projects.data.hosts)
      api(projects.data.playback)
      api(projects.data.playlists)
      api(projects.data.suggestions)
      api(projects.data.visualization)
      api(projects.domain)
      api(projects.ui.discover)
      api(projects.ui.mood)
      api(projects.ui.moods)
      api(projects.ui.player)
      api(projects.ui.playlists)
      api(projects.ui.search)

      implementation(libs.androidx.lifecycle.runtime.compose)
      implementation(libs.androidx.lifecycle.viewmodel.compose)

      implementation(libs.coil.compose)
      implementation(libs.haze)

      implementation(libs.kotlininject.runtime)
      implementation(libs.kotlinx.serialization.json)

      implementation(libs.material3.adaptive)
      implementation(libs.material3.adaptive.layout)
      implementation(libs.material3.adaptive.navigation)
      implementation(libs.material3.navigation.suite)
      implementation(libs.material3.windowSizeClass)

      implementation(libs.materialkolor)
      implementation(libs.materialyou)

      implementation(libs.navigation.compose)
    }
  }
}

ksp { arg("me.tatarka.inject.generateCompanionExtensions", "true") }

addKspDependencyForAllTargets(libs.kotlininject.compiler)
