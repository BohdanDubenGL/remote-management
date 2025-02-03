package com.globallogic.rdkb.remotemanagement.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.patch
import kotlinx.coroutines.CancellationException

suspend inline fun <T> HttpClient.safeCall(body: () -> T): Result<T> = runCatching(body)
    .onFailure { if (it is CancellationException) throw it }

suspend inline fun <reified T> HttpClient.safeGet(block: HttpRequestBuilder.() -> Unit): Result<T> =
    safeCall { get(block).body<T>() }

suspend inline fun <reified T> HttpClient.safePatch(block: HttpRequestBuilder.() -> Unit): Result<T> =
    safeCall { patch(block).body<T>() }
