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
  ":core:database",
  ":core:network",
  ":core:preferences",
  ":core:ui:compose",
  ":core:ui:resources",
  ":data:hosts",
  ":data:playlists",
  ":data:suggestions",
  ":data:test",
  ":data:visualization",
  ":domain",
  ":player",
  ":ui:favourites",
  ":ui:discover",
  ":ui:mood",
  ":ui:player",
  ":ui:search",
)
