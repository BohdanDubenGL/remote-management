package com.globallogic.rdkb.remotemanagement.domain.entity

data class AccessPointSettings(
    val accessPointGroup: AccessPointGroup,
    val accessPoints: List<AccessPoint>,
)

data class AccessPointGroup(
    val id: Int,
    val name: String,
)

data class AccessPoint(
    val enabled: Boolean,
    val band: String,
    val ssid: String,
    val availableSecurityModes: List<String>,
    val securityMode: String,
)
