package com.globallogic.rdkb.remotemanagement.data.permission

actual class PermissionController {
    actual fun checkPermission(permission: Permission): RequestResult = RequestResult.Granted

    actual suspend fun requestPermission(permission: Permission): RequestResult = RequestResult.Granted
}
