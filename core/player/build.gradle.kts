import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins { id("com.trm.audiofeels.kotlin.multiplatform") }

kotlin {
  android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()
    namespace = "com.trm.audiofeels.core.player"
    androidResources { enable = true }
  }

  targets.withType<KotlinNativeTarget>().forEach { nativeTarget ->
    nativeTarget.compilations.getByName("main") {
      val nskeyvalueobserving by cinterops.creating
    }
  }

  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.androidx.core.ktx)

        implementation(libs.androidx.media3.exoplayer)
        implementation(libs.androidx.media3.session)
        implementation(libs.androidx.media3.ui)

        implementation(libs.kotlinx.coroutines.guava)
      }
    }

    commonMain {
      dependencies {
        implementation(projects.core.base)
        implementation(projects.core.network)
        implementation(projects.domain)
      }
    }
  }
}
