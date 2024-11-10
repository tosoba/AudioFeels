package com.trm.audiofeels.data.hosts

import com.trm.audiofeels.core.base.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@Inject
@ApplicationScope
class HostsInMemoryDataSource {
  var host: String? = null
}
