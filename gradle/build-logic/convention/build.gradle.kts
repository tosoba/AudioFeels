plugins { `kotlin-dsl` }

java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.compose.gradlePlugin)
  compileOnly(libs.composeCompiler.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("kotlinMultiplatform") {
      id = "com.trm.audiofeels.kotlin.multiplatform"
      implementationClass = "com.trm.audiofeels.gradle.KotlinMultiplatformConventionPlugin"
    }

    register("root") {
      id = "com.trm.audiofeels.root"
      implementationClass = "com.trm.audiofeels.gradle.RootConventionPlugin"
    }

    register("kotlinAndroid") {
      id = "com.trm.audiofeels.kotlin.android"
      implementationClass = "com.trm.audiofeels.gradle.KotlinAndroidConventionPlugin"
    }

    register("androidApplication") {
      id = "com.trm.audiofeels.android.application"
      implementationClass = "com.trm.audiofeels.gradle.AndroidApplicationConventionPlugin"
    }

    register("androidLibrary") {
      id = "com.trm.audiofeels.android.library"
      implementationClass = "com.trm.audiofeels.gradle.AndroidLibraryConventionPlugin"
    }

    register("androidTest") {
      id = "com.trm.audiofeels.android.test"
      implementationClass = "com.trm.audiofeels.gradle.AndroidTestConventionPlugin"
    }

    register("compose") {
      id = "com.trm.audiofeels.compose"
      implementationClass = "com.trm.audiofeels.gradle.ComposeMultiplatformConventionPlugin"
    }
  }
}
