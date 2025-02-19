package com.globallogic.rdkb.remotemanagement.data.permission

import android.Manifest

actual class Permission(val nativePermission: String) {
    actual companion object {
        actual val Location: Permission = Permission(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
