package com.globallogic.rdkb.remotemanagement.data.wifi

import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo

actual class WifiScanner {
    actual suspend fun getCurrentWifi(): WifiInfo? = WifiInfo(ssid = "", bssid = "d8:3a:dd:40:5e:40") // fake

    actual suspend fun scanWifi(): List<WifiInfo> = emptyList()
}
