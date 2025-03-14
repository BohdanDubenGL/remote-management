package com.globallogic.rdkb.remotemanagement.data.wifi.impl.fake

import com.globallogic.rdkb.remotemanagement.data.wifi.WifiScanner
import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo

class FakeWifiScannerImpl : WifiScanner {
    override suspend fun getCurrentWifi(): WifiInfo? = null

    override suspend fun scanWifi(): List<WifiInfo> = emptyList()
}