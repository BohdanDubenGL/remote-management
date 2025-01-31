package com.globallogic.rdkb.remotemanagement.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header

expect fun PlatformHttpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

fun RdkCentralHttpClient(): HttpClient = PlatformHttpClient {
    defaultRequest {
        url(scheme = "http", host = "webpa.rdkcentral.com", port = 9003)
        header("Authorization", "Basic d3B1c2VyOndlYnBhQDEyMzQ1Njc4OTAK")
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.SIMPLE
    }
}
