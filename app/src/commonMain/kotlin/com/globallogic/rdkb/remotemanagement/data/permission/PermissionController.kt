package com.globallogic.rdkb.remotemanagement.data.permission

expect class PermissionController {
    fun checkPermission(permission: Permission): RequestResult

    suspend fun requestPermission(permission: Permission): RequestResult
}

enum class RequestResult {
    Denied, Granted, Rationale;
}
