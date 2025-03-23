package com.trm.audiofeels

import kotlinx.serialization.Serializable

sealed interface AppRoute {
  @Serializable data object Discover : AppRoute

  @Serializable data object Search : AppRoute
}
