plugins {
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.room)
}

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.data.database"
    androidResources { enable = true }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)

        implementation(libs.kotlinx.serialization.json)

        implementation(libs.sqlite.bundled)
        implementation(libs.room.runtime)
      }
    }
  }
}

dependencies {
  add("kspAndroid", libs.room.compiler)
  add("kspIosSimulatorArm64", libs.room.compiler)
  add("kspIosArm64", libs.room.compiler)
}

room { schemaDirectory("$projectDir/schemas") }
