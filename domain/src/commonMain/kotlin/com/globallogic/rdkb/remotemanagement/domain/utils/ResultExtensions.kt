package com.globallogic.rdkb.remotemanagement.domain.utils

import kotlinx.coroutines.CancellationException

inline fun <R> runCatchingSafe(block: () -> R): Result<R> {
    return runCatching(block)
        .onFailure { if (it is CancellationException) throw it }
        .onFailure { error ->
            println("Error: $error: ${error.stackTraceToString()}")
        }
}
