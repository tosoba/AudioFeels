plugins {
  id("com.trm.audiofeels.android.library")
  id("com.trm.audiofeels.kotlin.multiplatform")
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.room)
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.core.base)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlininject.runtime)
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
  add("kspIosX64", libs.room.compiler)
  add("kspIosArm64", libs.room.compiler)
}

room { schemaDirectory("$projectDir/schemas") }

android { namespace = "com.trm.audiofeels.core.database" }
