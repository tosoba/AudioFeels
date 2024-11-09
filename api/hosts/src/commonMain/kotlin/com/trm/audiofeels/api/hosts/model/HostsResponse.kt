package com.trm.audiofeels.api.hosts.model

import kotlinx.serialization.SerialName

data class HostsResponse(@SerialName("data") val hosts: List<String>?)
