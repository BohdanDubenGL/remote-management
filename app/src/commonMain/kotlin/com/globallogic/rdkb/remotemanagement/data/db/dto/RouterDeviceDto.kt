package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "router_device")
data class RouterDeviceDto(
    @PrimaryKey val macAddress: String,
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
    val availableBands: String,
    val updatedAt: Long,
)
