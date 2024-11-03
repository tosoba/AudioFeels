import com.trm.audiofeels.gradle.addKspDependencyForAllTargets
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  id("com.trm.audiofeels.android.application")
  id("com.trm.audiofeels.compose")
  alias(libs.plugins.ksp)
}

kotlin {
  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
  }

  targets.withType<KotlinNativeTarget>().forEach { nativeTarget ->
    nativeTarget.binaries.framework {
      baseName = "AudioFeels"
      isStatic = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(compose.preview)

      implementation(libs.androidx.activity.compose)
    }

    commonMain.dependencies {
      implementation(projects.api.audius)
      implementation(projects.core.base)
      implementation(projects.core.cache)
      implementation(projects.data.hosts)
      implementation(projects.data.playlists)
      implementation(projects.domain)

      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)

      implementation(libs.androidx.lifecycle.viewmodel)
      implementation(libs.androidx.lifecycle.runtime.compose)

      implementation(libs.kotlininject.runtime)
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

dependencies { debugImplementation(compose.uiTooling) }

ksp { arg("me.tatarka.inject.generateCompanionExtensions", "true") }

addKspDependencyForAllTargets(libs.kotlininject.compiler)
