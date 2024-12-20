package com.trm.audiofeels.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun httpClient(
  engine: HttpClientEngine? = null,
  config: HttpClientConfig<*>.() -> Unit,
): HttpClient = engine?.let { HttpClient(it, config) } ?: HttpClient(config)

fun HttpClientConfig<*>.configureDefault(
  logLevel: LogLevel? = null,
  expectSuccess: Boolean = true,
  followRedirects: Boolean = true,
  cacheStorage: CacheStorage? = null,
  maxRetries: Int? = null,
) {
  install(ContentNegotiation) {
    json(
      Json {
        encodeDefaults = true
        isLenient = true
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
        explicitNulls = false
      }
    )
  }

  logLevel?.let { install(Logging) { level = it } }

  cacheStorage?.let { install(HttpCache) { publicStorage(it) } }

  maxRetries?.let {
    install(HttpRequestRetry) {
      retryOnServerErrors(maxRetries = it)
      exponentialDelay()
    }
  }

  this.expectSuccess = expectSuccess
  this.followRedirects = followRedirects
}
