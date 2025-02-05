package com.globallogic.rdkb.remotemanagement.domain.utils

import kotlinx.coroutines.CancellationException

inline fun <R> runCatchingSafe(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }
}
