import com.trm.audiofeels.gradle.addKspDependencyForAllTargets
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.android.application")
  id("com.trm.audiofeels.compose")
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

  targets.withType<KotlinNativeTarget>().forEach { nativeTarget ->
    nativeTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(compose.preview)

      implementation(libs.androidx.activity.compose)

      implementation(libs.androidx.media3.exoplayer)
      implementation(libs.androidx.media3.session)
      implementation(libs.androidx.media3.ui)
    }

    commonMain.dependencies {
      implementation(projects.api.audius)
      implementation(projects.api.hosts)
      implementation(projects.core.base)
      implementation(projects.core.network)
      implementation(projects.core.player)
      implementation(projects.core.preferences)
      implementation(projects.core.ui.compose)
      implementation(projects.core.ui.resources)
      implementation(projects.data.database)
      implementation(projects.data.hosts)
      implementation(projects.data.playback)
      implementation(projects.data.playlists)
      implementation(projects.data.suggestions)
      implementation(projects.data.visualization)
      implementation(projects.domain)
      implementation(projects.ui.discover)
      implementation(projects.ui.mood)
      implementation(projects.ui.moods)
      implementation(projects.ui.player)
      implementation(projects.ui.playlists)
      implementation(projects.ui.search)

      implementation(libs.androidx.lifecycle.runtime.compose)
      implementation(libs.androidx.lifecycle.viewmodel.compose)

      implementation(libs.coil.compose)
      implementation(libs.haze)

      implementation(libs.kotlininject.runtime)
      implementation(libs.kotlinx.serialization.json)

      implementation(libs.material3.adaptive)
      implementation(libs.material3.adaptive.layout)
      implementation(libs.material3.adaptive.navigation)
      implementation(compose.material3AdaptiveNavigationSuite)

      implementation(libs.materialkolor)
      implementation(libs.materialyou)

      implementation(libs.navigation.compose)
    }
  }
}

android {
  namespace = "com.trm.audiofeels"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "com.trm.audiofeels"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }

  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }

  buildTypes { getByName("release") { isMinifyEnabled = false } }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

ksp { arg("me.tatarka.inject.generateCompanionExtensions", "true") }

addKspDependencyForAllTargets(libs.kotlininject.compiler)
