package com.trm.audiofeels.data.hosts

import com.trm.audiofeels.core.base.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class AudiusHostsInMemoryDataSource {
  var host: String? = null
}
