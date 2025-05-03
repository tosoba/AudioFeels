rootProject.name = "AudioFeels"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  includeBuild("gradle/build-logic")

  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
  }
}

include(
  ":api:audius",
  ":api:hosts",
  ":composeApp",
  ":core:base",
  ":core:network",
  ":core:player",
  ":core:preferences",
  ":core:ui:compose",
  ":core:ui:resources",
  ":data:database",
  ":data:hosts",
  ":data:playback",
  ":data:playlists",
  ":data:suggestions",
  ":data:test",
  ":data:visualization",
  ":domain",
  ":ui:favourites",
  ":ui:discover",
  ":ui:mood",
  ":ui:moods",
  ":ui:player",
  ":ui:playlists",
  ":ui:search",
)
