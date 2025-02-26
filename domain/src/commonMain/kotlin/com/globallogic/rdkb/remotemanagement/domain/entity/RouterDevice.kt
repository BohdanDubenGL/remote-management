package com.globallogic.rdkb.remotemanagement.domain.entity

data class RouterDevice(
    val modelName: String,
    val manufacturer: String,
    val ipAddressV4: String,
    val ipAddressV6: String,
    val macAddress: String,
    val firmwareVersion: String,
    val serialNumber: String,
    val totalMemory: Long,
    val freeMemory: Long,
    val availableBands: Set<String>,
) {
    val freeMemoryPercent: Double get() = 100.0 * freeMemory / totalMemory
}
