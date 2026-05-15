package com.trm.audiofeels.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.HasUnitTestBuilder
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

fun Project.configureAndroid() {
  val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
  val compileSdkConfig = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
  val minSdkConfig = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
  val targetSdkConfig = libs.findVersion("android-targetSdk").get().requiredVersion.toInt()

  pluginManager.withPlugin("com.android.application") {
    extensions.configure<ApplicationExtension>("android") {
      compileSdk = compileSdkConfig

      defaultConfig {
        minSdk = minSdkConfig
        targetSdk = targetSdkConfig
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders += mapOf("appAuthRedirectScheme" to "empty")
      }

      compileOptions { isCoreLibraryDesugaringEnabled = true }

      testOptions {
        unitTests {
          isIncludeAndroidResources = true
          isReturnDefaultValues = true
        }
      }
    }

    extensions.configure<ApplicationAndroidComponentsExtension>("androidComponents") {
      beforeVariants(selector().withBuildType("release")) { variantBuilder ->
        (variantBuilder as? HasUnitTestBuilder)?.apply { enableUnitTest = false }
      }
    }
  }

  pluginManager.withPlugin("com.android.library") {
    extensions.configure<LibraryExtension>("android") {
      compileSdk = compileSdkConfig

      defaultConfig {
        minSdk = minSdkConfig
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders += mapOf("appAuthRedirectScheme" to "empty")
      }

      compileOptions { isCoreLibraryDesugaringEnabled = true }

      testOptions {
        unitTests {
          isIncludeAndroidResources = true
          isReturnDefaultValues = true
        }
      }
    }

    extensions.configure<LibraryAndroidComponentsExtension>("androidComponents") {
      beforeVariants(selector().withBuildType("release")) { variantBuilder ->
        (variantBuilder as? HasUnitTestBuilder)?.apply { enableUnitTest = false }
      }
    }
  }

  dependencies { "coreLibraryDesugaring"(libs.findLibrary("tools.desugarjdklibs").get()) }
}
