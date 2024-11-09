package com.trm.audiofeels.core.preferences

internal const val dataStoreFileName = "audio_feels.preferences_pb"

internal interface DataStorePathBuilder {
  operator fun invoke(): String
}

internal expect class DataStorePlatformPathBuilder : DataStorePathBuilder
