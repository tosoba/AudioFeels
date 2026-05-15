import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.domain"
    androidResources { enable = true }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)

        implementation(libs.kotlinx.serialization.json)
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
