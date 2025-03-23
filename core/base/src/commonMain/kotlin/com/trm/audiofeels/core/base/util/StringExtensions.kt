package com.trm.audiofeels.core.base.util

fun String.trimHttps(): String = replace(HTTPS_PREFIX, "")

const val HTTPS_PREFIX = "https://"
