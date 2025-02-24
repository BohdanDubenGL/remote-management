package com.globallogic.rdkb.remotemanagement.domain.entity

data class RouterDevice(
    val lanConnected: Boolean,
    val modelName: String,
    val ipAddress: String,
    val macAddress: String,
    val firmwareVersion: String,
    val additionalFirmwareVersion: String,
    val serialNumber: String,
    val totalMemory: Long,
    val freeMemory: Long,
    val availableBands: Set<String>,
) {
    val freeMemoryPercent: Double get() = 100.0 * freeMemory / totalMemory
}
