package com.globallogic.rdkb.remotemanagement.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.time.Duration.Companion.seconds

expect fun PlatformHttpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

fun RdkCentralHttpClient(): HttpClient = PlatformHttpClient {
    defaultRequest {
        url(scheme = "http", host = "webpa.rdkcentral.com", port = 9003)
        header("Authorization", "Basic d3B1c2VyOndlYnBhQDEyMzQ1Njc4OTAK")
    }
    install(Logging) {
        level = LogLevel.NONE
        logger = Logger.SIMPLE
    }
    install(HttpTimeout) {
        socketTimeoutMillis = 30.seconds.inWholeMilliseconds
        connectTimeoutMillis = 30.seconds.inWholeMilliseconds
        requestTimeoutMillis = 30.seconds.inWholeMilliseconds
    }
    install(ContentNegotiation) {
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
        }
        json(json = json)
        xml(format = XML {
            xmlDeclMode = XmlDeclMode.Charset
        })
        register(ContentType.Text.Plain, KotlinxSerializationConverter(json))
    }
}
