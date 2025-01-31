package com.globallogic.rdkb.remotemanagement.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.js.Js

actual fun PlatformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(Js) {
    config(this)
}
