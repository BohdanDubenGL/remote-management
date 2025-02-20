package com.globallogic.rdkb.remotemanagement.data.wifi

import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo

actual class WifiScanner {
    actual suspend fun getCurrentWifi(): WifiInfo? = null

    actual suspend fun scanWifi(): List<WifiInfo> = emptyList()
}
