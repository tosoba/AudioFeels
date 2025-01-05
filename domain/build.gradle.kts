import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.parcelize)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)

        implementation(libs.kotlinx.coroutines.core)
      }
    }
  }

  targets.configureEach {
    val isAndroidTarget = platformType == KotlinPlatformType.androidJvm
    compilations.configureEach {
      compileTaskProvider.configure {
        compilerOptions {
          if (isAndroidTarget) {
            freeCompilerArgs.addAll(
              "-P",
              "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.trm.audiofeels.core.base.model.CommonParcelize",
            )
          }
        }
      }
    }
  }
}

android { namespace = "com.trm.audiofeels.domain" }
