package com.globallogic.rdkb.remotemanagement.data.wifi.impl.fake

import com.globallogic.rdkb.remotemanagement.data.wifi.WifiScanner
import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo

class FakeWifiScannerImpl : WifiScanner {
    override suspend fun getCurrentWifi(): WifiInfo? = WifiInfo(ssid = "", bssid = "ca:3b:e1:9b:ba:8d") // fake

    override suspend fun scanWifi(): List<WifiInfo> = emptyList()
}