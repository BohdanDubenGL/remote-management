package com.globallogic.rdkb.remotemanagement.domain.entity

data class WifiSettings(
    val wifi: List<Wifi>
)

data class Wifi(
    val band: String,
    val ssid: String,
)
