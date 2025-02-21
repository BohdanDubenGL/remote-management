package com.globallogic.rdkb.remotemanagement.data.wifi

import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo

actual class WifiScanner {
    actual suspend fun getCurrentWifi(): WifiInfo? = WifiInfo(ssid = "", bssid = "dc:a6:32:0e:b8:bb") // fake

    actual suspend fun scanWifi(): List<WifiInfo> = emptyList()
}
