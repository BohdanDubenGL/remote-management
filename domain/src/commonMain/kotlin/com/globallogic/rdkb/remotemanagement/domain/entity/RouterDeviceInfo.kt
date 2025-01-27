package com.globallogic.rdkb.remotemanagement.domain.entity

data class RouterDeviceInfo(
    val lanConnected: Boolean,
    val connectedExtender: Int,
    val modelName: String,
    val ipAddress: String,
    val firmwareVersion: String,
    val serialNumber: String,
    val processorLoadPercent: Int,
    val memoryUsagePercent: Int,
    val totalDownloadTraffic: Long,
    val totalUploadTraffic: Long,
    val availableBands: Set<String>,
) {
    companion object {
        val empty: RouterDeviceInfo = RouterDeviceInfo(true, 0, "", "", "", "", 0, 0, 0L, 0L, emptySet())
    }
}
