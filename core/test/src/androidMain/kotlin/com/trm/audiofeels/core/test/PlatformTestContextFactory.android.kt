package com.trm.audiofeels.core.test

import androidx.test.core.app.ApplicationProvider
import com.trm.audiofeels.core.base.util.PlatformContext

actual fun platformTestContext(): PlatformContext = ApplicationProvider.getApplicationContext()
