package com.trm.audiofeels.core.preferences

import com.trm.audiofeels.core.base.util.PlatformContext

internal const val dataStoreFileName = "audio_feels.preferences_pb"

expect val PlatformContext.dataStorePath: String
