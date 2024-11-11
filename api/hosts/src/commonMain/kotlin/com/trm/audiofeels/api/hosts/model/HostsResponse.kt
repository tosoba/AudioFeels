package com.trm.audiofeels.api.hosts.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class HostsResponse(@SerialName("data") val hosts: List<String>?)
