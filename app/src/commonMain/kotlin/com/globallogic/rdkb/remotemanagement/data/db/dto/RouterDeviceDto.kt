package com.globallogic.rdkb.remotemanagement.data.db.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "router_device")
data class RouterDeviceDto(
    @PrimaryKey val macAddress: String,
    val lanConnected: Boolean,
    val modelName: String,
    val manufacturer: String,
    val ipAddressV4: String,
    val ipAddressV6: String,
    val firmwareVersion: String,
    val serialNumber: String,
    val totalMemory: Long,
    val freeMemory: Long,
    val availableBands: String,
    val updatedAt: Long,
)
