package com.globallogic.rdkb.remotemanagement.domain.entity

data class RouterDevice(
    val lanConnected: Boolean,
    val connectedExtender: Int,
    val modelName: String,
    val ipAddress: String,
    val macAddress: String,
    val firmwareVersion: String,
    val serialNumber: String,
    val processorLoadPercent: Int,
    val memoryUsagePercent: Int,
    val totalDownloadTraffic: Long,
    val totalUploadTraffic: Long,
    val availableBands: Set<String>,
)
