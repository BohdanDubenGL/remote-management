package com.globallogic.rdkb.remotemanagement.data.wifi

import com.globallogic.rdkb.remotemanagement.data.wifi.model.WifiInfo

interface WifiScanner {
    suspend fun getCurrentWifi(): WifiInfo?
    suspend fun scanWifi(): List<WifiInfo>
}
