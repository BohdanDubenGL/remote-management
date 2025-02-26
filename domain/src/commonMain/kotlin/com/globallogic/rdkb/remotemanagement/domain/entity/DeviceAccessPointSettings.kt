package com.globallogic.rdkb.remotemanagement.domain.entity

data class DeviceAccessPointSettings(
    val bands: List<BandSettings>
)

data class BandSettings(
    val frequency: String,
    val ssid: String,
    val password: String,
    val securityMode: String,
    val availableSecurityModes: List<String>,
    val enabled: Boolean,
    val sameAsFirst: Boolean,
)
