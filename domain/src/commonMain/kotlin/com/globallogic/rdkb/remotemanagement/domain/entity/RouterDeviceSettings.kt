package com.globallogic.rdkb.remotemanagement.domain.entity

data class RouterDeviceSettings(
    val bands: List<BandSettings>
)

data class BandSettings(
    val frequency: String,
    val ssid: String,
    val password: String
)
