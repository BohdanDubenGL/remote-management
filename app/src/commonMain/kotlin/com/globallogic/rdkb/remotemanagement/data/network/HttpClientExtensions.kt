package com.globallogic.rdkb.remotemanagement.data.network

import com.globallogic.rdkb.remotemanagement.domain.utils.Resource
import com.globallogic.rdkb.remotemanagement.domain.utils.ThrowableResourceError
import com.globallogic.rdkb.remotemanagement.domain.utils.runCatchingSafe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post

inline fun <T> HttpClient.safeCall(body: () -> T): Resource<T, ThrowableResourceError> = runCatchingSafe(body).fold(
    onSuccess = { result -> Resource.Success(result) },
    onFailure = { throwable -> Resource.Failure(ThrowableResourceError(throwable)) }
)

suspend inline fun <reified T> HttpClient.safeGet(block: HttpRequestBuilder.() -> Unit): Resource<T, ThrowableResourceError> =
    safeCall { get(block).body<T>() }

suspend inline fun <reified T> HttpClient.safePost(block: HttpRequestBuilder.() -> Unit): Resource<T, ThrowableResourceError> =
    safeCall { post(block).body<T>() }

suspend inline fun <reified T> HttpClient.safePatch(block: HttpRequestBuilder.() -> Unit): Resource<T, ThrowableResourceError> =
    safeCall { patch(block).body<T>() }
