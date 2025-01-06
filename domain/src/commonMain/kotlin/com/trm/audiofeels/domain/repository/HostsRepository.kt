package com.trm.audiofeels.domain.repository

interface HostsRepository {
  suspend fun fetchHost(): String

  suspend fun retrieveHost(): String
}
