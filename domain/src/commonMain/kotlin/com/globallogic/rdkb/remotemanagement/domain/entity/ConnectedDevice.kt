package com.globallogic.rdkb.remotemanagement.domain.entity

data class ConnectedDevice(
    val isActive: Boolean,
    val macAddress: String,
    val hostName: String,
    val ipAddress: String,
    val vendorClassId: String,
    val stats: ConnectedDeviceStats,
)
