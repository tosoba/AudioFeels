package com.trm.audiofeels

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform
