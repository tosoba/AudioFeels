package com.trm.audiofeels.core.base.di

import android.app.Service
import android.content.Context
import me.tatarka.inject.annotations.Provides

interface BaseServiceComponent {
  val service: Service

  @Provides fun bindServiceContext(): @ServiceContext Context = service
}
