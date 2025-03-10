package com.trm.audiofeels.domain.repository

interface HostsRepository {
  suspend fun clearHost()

  suspend fun retrieveHost(): String
}
