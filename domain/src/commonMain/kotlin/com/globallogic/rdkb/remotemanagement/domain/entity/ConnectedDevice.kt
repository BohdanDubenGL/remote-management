package com.globallogic.rdkb.remotemanagement.domain.entity

data class ConnectedDevice(
    val macAddress: String,
    val hostName: String,
    val ssid: String,
    val channel: Int,
    val rssi: Int,
    val bandWidth: String
)
